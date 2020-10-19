package io.bytetrend.geo.location.services;

import io.bytetrend.geo.location.model.*;
import io.bytetrend.geo.location.services.exception.ServiceException;
import io.bytetrend.geo.location.sink.dao.NoSqlLocationDao;
import io.bytetrend.geo.location.source.FileUtils;
import io.bytetrend.geo.location.source.PointOfInterestLoader;
import io.bytetrend.geo.location.source.loader.csv.CsvLoaderImpl;
import io.bytetrend.geo.location.source.model.FileLocation;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static java.io.File.separatorChar;

@Service
public class PointOfInterestServiceImpl implements PointOfInterestService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PointOfInterestServiceImpl.class);

    @Autowired
    PointOfInterestLoader<FileLocation, Location> fastFoodLocationLoader;
    @Autowired
    NoSqlLocationDao fastFoodLocationDao;

    @Value("${location.fastfood.staging.directory}")
    private String stagingDirectory;
    @Value("${location.fastfood.data.file}")
    private String dataFile;


    @Override
    public List<GeoResult> searchLocations(GeoLocationRequest request) throws ServiceException {
        Set<GeoResult> result = new TreeSet<>();
        try {
            Optional<List<Location>> locations = fastFoodLocationDao.findLocations(request);
            if (locations.isPresent()) {
                for (Location l : locations.get()) {
                    GeoResult geo = new GeoResult(
                            new PointOfInterest(l, l.getLocationType()),
                            request.getGeoLocation());
                    if(geo.getDistance() <= request.getRadius())
                        result.add(geo);
                }
            }
        } catch (Exception e) {
            LOGGER.error(ExceptionUtils.getMessage(e), e);
            throw new ServiceException("Error searching for locations with " +
                    " lat {} lon: {} max lat ");
        }
        return result.stream().limit(request.getMaxItems()).collect(Collectors.toList());
    }

    @Override
    public int loadLocation() throws ServiceException {
        int count;
        try {
            final String sourceFilePath = stagingDirectory + separatorChar + dataFile;
            LOGGER.info("Running loading location data file {}", sourceFilePath);
            final Resource resource = FileUtils.getResource(sourceFilePath);
            count = fastFoodLocationLoader.processLocations(resource,
                    CsvLoaderImpl.FAST_FOOD.getReader(),
                    fastFoodLocationDao, new Converter<FileLocation, Location>() {
                        final Location.Builder builder = new Location.Builder();

                        @Override
                        public Location convertTo(FileLocation fileLocation) {
                            return builder.convert(fileLocation).build();
                        }
                    });
            LOGGER.info("Inserted {} locations is fast food database", count);
        } catch (Exception e) {
            LOGGER.error(ExceptionUtils.getMessage(e));
            throw new ServiceException("Exception while loading data. ", e);
        }
        return count;
    }
}
