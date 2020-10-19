package io.bytetrend.geo.location.sink.dao;


import com.datastax.driver.core.*;
import com.datastax.driver.core.exceptions.NoHostAvailableException;
import com.datastax.driver.core.exceptions.QueryExecutionException;
import com.datastax.driver.core.exceptions.QueryValidationException;
import com.datastax.driver.core.exceptions.UnsupportedFeatureException;
import com.google.common.base.Stopwatch;
import io.bytetrend.geo.location.model.GeoLocationRequest;
import io.bytetrend.geo.location.model.GeoLocationRequest.GeoLocationBounds;
import io.bytetrend.geo.location.model.Location;
import io.bytetrend.geo.location.model.LocationType;
import io.bytetrend.geo.location.sink.DataSink;
import io.bytetrend.geo.location.sink.connector.CassandraConnector;
import io.bytetrend.geo.location.sink.connector.exception.ReadFromDBException;
import io.bytetrend.geo.location.source.loader.SourceSinkColumnNames;
import io.bytetrend.geo.location.source.loader.fastfood.FastFoodToFileLocation;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static java.util.Optional.empty;

@Service
public class NoSqlLocationDaoImpl implements NoSqlLocationDao, DataSink<Location> {
    public static final Logger LOGGER = LoggerFactory.getLogger(NoSqlLocationDaoImpl.class);

    @Value("${location.database.batch.minReplicas}")
    private int minReplicas;
    @Value("${location.database.readTimeout}")
    private int readTimeout;
    @Value("${location.database.insert.batch.maximumRows}")
    private int maximumRows;
    @Value("${location.database.insert.batch.consistency}")
    private String insertConsistencyLevel;
    private static final String fetchAll = "SELECT * FROM sandbox.location";
    private static final String searchQuery = "SELECT * FROM sandbox.location WHERE sequence in :latitudes AND LONGITUDE >= :minlongitude AND LONGITUDE < :maxlongitude ";

    @Autowired
    private CassandraConnector cassandraDao;

    private PreparedStatement searchQueryStmt;
    private PreparedStatement fetchAllStmt;
    private PreparedStatement insertStmt;

    /**
     * Initialize DB Statements
     */
    public void init() {
        if (cassandraDao != null) {
            searchQueryStmt = cassandraDao.getSession().prepare(searchQuery);
            LOGGER.info("Prepare statement for search query {} successfully completed.", searchQuery);
            fetchAllStmt = cassandraDao.getSession().prepare(fetchAll);
            StringBuilder names = new StringBuilder("INSERT INTO sandbox.location(sequence, ");
            StringBuilder values = new StringBuilder("?, ");
            for (int i = 0; i < SourceSinkColumnNames.fileLocationFieldNames.length; i++) {
                names.append(SourceSinkColumnNames.fileLocationFieldNames[i]);
                values.append("?");
                if (i < SourceSinkColumnNames.fileLocationFieldNames.length - 1) {
                    names.append(",");
                    values.append(",");
                }
            }

            values.append(")");
            names.append(") VALUES (");
            final String insertSQL = names.append(values).toString();
            LOGGER.info("Location insert statement built: {}", insertSQL);
            insertStmt = cassandraDao.getSession().prepare(insertSQL);
            insertStmt.setConsistencyLevel(ConsistencyLevel.LOCAL_QUORUM);
            LOGGER.debug("Prepared statement for insertSQL completed successfully");
        }
    }

    private List<Integer> getLatitudes(GeoLocationRequest request) {
        List<Integer> list = new ArrayList<>();
        for (int i = (int) request.getGeoLocationBounds().getMinLatitude();
             i < request.getGeoLocationBounds().getMaxLatitude(); i++) {
            list.add(i);
        }
        return list;
    }

    @Override
    public Optional<List<Location>> findLocations(GeoLocationRequest request) throws LocationDaoException {
        Stopwatch st = Stopwatch.createStarted();
        try {
            Session session = cassandraDao.getSession();
            if (session == null) {
                throw new ReadFromDBException("Invalid Cassandra session.");
            }
            if (searchQueryStmt == null) {
                init();
            }
            GeoLocationBounds bounds = request.getGeoLocationBounds();
            List<Integer> latitudes = getLatitudes(request);
            LOGGER.debug("Searching locations with parameters latitudes {} min-longitude {} max-longitude {}",
                    ArrayUtils.toString(latitudes), bounds.getMinLongitude(), bounds.getMaxLongitude());
            final BoundStatement bs = searchQueryStmt.bind();
            bs.setList("latitudes", latitudes);
            bs.setFloat("minlongitude", bounds.getMinLongitude());
            bs.setFloat("maxlongitude", bounds.getMaxLongitude());
            final ResultSetFuture future = cassandraDao.executeAsynchronous(bs);
            final ResultSet resultSet = future.getUninterruptibly(readTimeout, TimeUnit.MILLISECONDS);
            final List<Row> dbRows = resultSet.all();
            return mapRowToLocation(dbRows, bounds.getMinLatitude(),
                    bounds.getMaxLatitude(), true);
        } catch (TimeoutException t) {
            LOGGER.error(ExceptionUtils.getStackTrace(t));
            throw new LocationDaoException("Future timeout waiting for Cassandra response" + t.getMessage(), t);
        } catch (ReadFromDBException | NoHostAvailableException | QueryExecutionException r) {
            searchQueryStmt = null;
            LOGGER.error(ExceptionUtils.getStackTrace(r));
            throw new LocationDaoException("Cassandra server related exception " + r.getMessage(), r);
        } catch (UnsupportedFeatureException | QueryValidationException u) {
            LOGGER.error(ExceptionUtils.getStackTrace(u));
            throw new LocationDaoException("Cassandra query Exception " + u.getMessage(), u);
        } catch (Exception e) {
            LOGGER.error(ExceptionUtils.getStackTrace(e));
            throw new LocationDaoException("Exception retrieving location data.");
        } finally {
            LOGGER.info("Find locations took {} ms.", st.elapsed(TimeUnit.MILLISECONDS));
        }

    }

    /**
     * @param locations list of locations to insert
     * @return amount inserted
     */
    @Override
    public int insertLocations(List<Location> locations) throws LocationDaoException {
        Stopwatch st = Stopwatch.createStarted();
        try {
            Session session = cassandraDao.getSession();
            if (session == null) {
                throw new LocationDaoException("Cassandra session is null");
            }
            if (insertStmt == null) {
                init();
            }
            ArrayList<Statement> list = new ArrayList<>();
            for (Location loc : locations) {
                int sequence = loc.getLatitude().intValue();
                list.add(insertStmt.bind(
                        sequence,
                        loc.getAddress().getAddress1(),
                        loc.getAddress().getCity(),
                        loc.getAddress().getCountry(),
                        loc.getLatitude(),
                        loc.getLongitude(),
                        loc.getName(),
                        loc.getAddress().getZipCode(),
                        loc.getAddress().getState(),
                        loc.getWebsite(),
                        loc.getLocationType().name()
                ));
            }
            run(list);
            return locations.size();
        } catch (NoHostAvailableException | QueryValidationException e) {
            insertStmt = null;
            LOGGER.error(ExceptionUtils.getStackTrace(e));
            throw new LocationDaoException("Error inserting location " + e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.error(ExceptionUtils.getStackTrace(e));
            throw new LocationDaoException("Error inserting location " + e.getMessage(), e);
        } finally {
            LOGGER.info("Inserting {} records took {} ms.", locations.size(),st.elapsed(TimeUnit.MILLISECONDS));
        }
    }

    @Override
    public Optional<List<Location>> fetchAll() throws LocationDaoException {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            Session session = cassandraDao.getSession();
            if (session == null) {
                throw new ReadFromDBException("Cassandra session is null");
            }
            if (fetchAllStmt == null)
                init();
            final BoundStatement boundStmt = fetchAllStmt.bind();
            final ResultSetFuture future = cassandraDao.executeAsynchronous(boundStmt);
            final ResultSet resultSet = future.getUninterruptibly(readTimeout, TimeUnit.MILLISECONDS);
            final List<Row> dbRows = resultSet.all();
            return mapRowToLocation(dbRows, 0, 0, false);
        } catch (ReadFromDBException | NoHostAvailableException | QueryExecutionException e) {
            fetchAllStmt = null;
            LOGGER.error(ExceptionUtils.getStackTrace(e));
            throw new LocationDaoException(e.getMessage(), e);
        } catch (UnsupportedFeatureException | QueryValidationException | TimeoutException e) {
            LOGGER.error(ExceptionUtils.getStackTrace(e));
            throw new LocationDaoException(e.getMessage(), e);
        } finally {
            LOGGER.info("Fetching all locations took {}", sw.elapsed(TimeUnit.MILLISECONDS));
        }
    }

    /**
     * method to convert from a cassandra row to a Location object.
     */
    private Optional<List<Location>> mapRowToLocation(final List<Row> dbRowList,
                                                      final float minLatitude,
                                                      final float maxLatitude,
                                                      boolean filterByLatitude) {

        if (dbRowList != null) {
            final List<Location> locations = dbRowList.stream().map(row -> {
                Location location = null;
                try {
                    Location.Builder builder = new Location.Builder();
                    float latitude = row.getFloat(FastFoodToFileLocation.LATITUDE.getCanonicalFieldName());
                    if (!filterByLatitude || isLatitudeInRange(latitude, minLatitude, maxLatitude)) {
                        location = builder.setLatitude(latitude)
                                .setLongitude(row.getFloat(FastFoodToFileLocation.LONGITUDE.getCanonicalFieldName()))
                                .setName(row.getString(FastFoodToFileLocation.NAME.getCanonicalFieldName()))
                                .setAddress(row.getString(FastFoodToFileLocation.ADDRESS.getCanonicalFieldName()))
                                .setCity(row.getString(FastFoodToFileLocation.CITY.getCanonicalFieldName()))
                                .setState(row.getString(FastFoodToFileLocation.STATE.getCanonicalFieldName()))
                                .setZip(row.getString(FastFoodToFileLocation.POSTALCODE.getCanonicalFieldName()))
                                .setCountry(row.getString(FastFoodToFileLocation.COUNTRY.getCanonicalFieldName()))
                                .setWebsite(row.getString(FastFoodToFileLocation.WEBSITE.getCanonicalFieldName()))
                                .setLocationType(LocationType.valueOf(row.getString(FastFoodToFileLocation.CATEGORY.getCanonicalFieldName())))
                                .build();
                    }
                } catch (Exception e) {
                    LOGGER.error("Error reading location from database reason: {}", e.getMessage());
                }
                return Optional.ofNullable(location);
            }).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
            return Optional.of(locations);
        }
        return empty();
    }

    /**
     * Returns true if the latitude is within the parameters.
     *
     * @param latitude    latitude of the location
     * @param minLatitude minimum latitude
     * @param maxLatitude maximum latitude
     * @return true/false
     */
    private boolean isLatitudeInRange(final double latitude, final double minLatitude,
                                      final double maxLatitude) {
        return latitude < maxLatitude && latitude >= minLatitude;
    }

    private void run(ArrayList<Statement> list) {
        ArrayList<Statement> statements = new ArrayList<>();
        LOGGER.debug("Inserting {} records in batch mode", list.size());
        for (Statement st : list) {
            statements.add(st);
            if (statements.size() < maximumRows)
                continue;

            execute(statements);
            LOGGER.debug("Inserted {} out of {}", statements.size(), list.size());
            statements.clear();
        }
    }

    private void execute(List<Statement> list) {
        List<List<Statement>> groups = splitByToken(list);
        for (List<Statement> group : groups) {
            try {
                BatchStatement batch = new BatchStatement(BatchStatement.Type.UNLOGGED);
                batch.setConsistencyLevel(ConsistencyLevel.valueOf(insertConsistencyLevel));
                batch.addAll(group);
                ResultSet resultSet = cassandraDao.getSession().execute(batch);
                resultSet.wasApplied();
            } catch (Exception e) {
                LOGGER.error("Error executing batch statement reason {}", e.getMessage(), e);
            }
        }
    }

    List<List<Statement>> splitByToken(List<Statement> batch) {
        Map<Set<Host>, List<Statement>> batches = new HashMap<>();
        for (Statement s : batch) {
            Set<Host> hosts = new HashSet<>();
            int replicas = 0;
            Iterator<Host> it = cassandraDao.getCluster().getConfiguration().getPolicies()
                    .getLoadBalancingPolicy().newQueryPlan(s.getKeyspace(), s);
            while (it.hasNext() && replicas < minReplicas) {
                hosts.add(it.next());
                replicas++;
            }
            List<Statement> tokenBatch = batches.computeIfAbsent(hosts, hosts1 -> new ArrayList<>());
            tokenBatch.add(s);
        }
        return new ArrayList<>(batches.values());
    }

    @Override
    public int save(List<Location> data) throws LocationDaoException {
        int count = insertLocations(data);
        LOGGER.info("Inserted {} rows to database out of {} records", count, data.size());
        return count;
    }
}
