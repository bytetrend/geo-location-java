package io.bytetrend.geo.location.sink.connector;


import com.datastax.driver.core.*;
import com.datastax.driver.core.exceptions.NoHostAvailableException;
import com.datastax.driver.core.exceptions.QueryExecutionException;
import com.datastax.driver.core.exceptions.QueryValidationException;
import com.datastax.driver.core.exceptions.UnsupportedFeatureException;
import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;
import com.datastax.driver.core.policies.LoadBalancingPolicy;
import com.datastax.driver.core.policies.RoundRobinPolicy;
import com.datastax.driver.core.policies.TokenAwarePolicy;
import io.netty.util.internal.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class CassandraConnector {

    private static final Logger logger = LoggerFactory.getLogger(CassandraConnector.class);
    private static final Logger dbProfileLogger = LoggerFactory.getLogger("cass_profile");

    private static final long REFRESH_RESPONSE_TIMEOUT = 50; //timeout in milliseconds

    private final Cluster cluster;
    private final Session session;
    private final boolean profilingDBCalls = false;
    final String testQuery = "select * from dummy_table where 1=1";

    public Cluster getCluster() {
        return cluster;
    }

    public Session getSession() {
        return session;
    }

    public CassandraConnector(String server1, String server2, int port, String keyspace, String datacenter,
                              String userid, String password, boolean useSSL) {
        LoadBalancingPolicy roundRobinPolicy;
        if (StringUtils.trimToEmpty(datacenter).isEmpty()) {
            roundRobinPolicy = new RoundRobinPolicy();
        } else {
            roundRobinPolicy = DCAwareRoundRobinPolicy.builder()
                    .withLocalDc(datacenter).withUsedHostsPerRemoteDc(0)
                    .build();
            //new DCAwareRoundRobinPolicy(datacenter,0,false,true);
        }
        Cluster.Builder builder = new Cluster.Builder().withoutJMXReporting()
                .addContactPoints(server1, server2)
                .withPort(port).withLoadBalancingPolicy(new TokenAwarePolicy(roundRobinPolicy, true))
                .withCredentials(userid, password);
        if (useSSL) {
            builder.withSSL();
        }
        cluster = builder.build();

        this.session = cluster.connect(keyspace);
        Metadata metadata = cluster.getMetadata();
        logger.info(String.format("Connected to cluster: %s\n", metadata.getClusterName()));
        for (Host host : metadata.getAllHosts()) {
            logger.info(String.format("Datacenter: %s; Host: %s; Rack: %s\n", host.getDatacenter(), host.getAddress(), host.getRack()));
        }
    }

    public CassandraConnector(String server1, String server2, int port, String keyspace, String datacenter, String userid,
                              String password, String localCorePoolSize, String localMaxPoolSize, String remoteCorePollSize,
                              String remoteMaxPoolSize) {
        this(server1, server2, port, keyspace, datacenter, userid, password, false, localCorePoolSize, localMaxPoolSize,
                remoteCorePollSize, remoteMaxPoolSize);
    }

    public CassandraConnector(String server1, String server2, int port, String keyspace, String datacenter,
                              String userid, String password, boolean useSSL, String localCorePoolSize, String localMaxPoolSize,
                              String remoteCorePoolSize, String remoteMaxPoolSize) {
        LoadBalancingPolicy roundRobinPolicy;
        if (StringUtils.trimToEmpty(datacenter).isEmpty()) {
            roundRobinPolicy = new RoundRobinPolicy();
        } else {
            roundRobinPolicy = DCAwareRoundRobinPolicy.builder().withLocalDc(datacenter).build();
        }
        PoolingOptions poolingOptions = new PoolingOptions();
        if (!StringUtils.trimToEmpty(localCorePoolSize).isEmpty() && StringUtils.isNumeric(localCorePoolSize)) {
            localCorePoolSize = StringUtils.trim(localCorePoolSize);
            int ilocalCorePoolSize = Integer.parseInt(localCorePoolSize);
            poolingOptions.setCoreConnectionsPerHost(HostDistance.LOCAL, ilocalCorePoolSize);
        }
        if (!StringUtils.trimToEmpty(localMaxPoolSize).isEmpty() && StringUtils.isNumeric(localMaxPoolSize)) {
            localMaxPoolSize = StringUtils.trim(localMaxPoolSize);
            int ilocalMaxPoolSize = Integer.parseInt(localMaxPoolSize);
            poolingOptions.setMaxConnectionsPerHost(HostDistance.LOCAL, ilocalMaxPoolSize);
        }
        if (!StringUtils.trimToEmpty(remoteCorePoolSize).isEmpty() && StringUtils.isNumeric(remoteCorePoolSize)) {
            remoteCorePoolSize = StringUtils.trim(remoteCorePoolSize);
            int iremoteCorePoolSize = Integer.parseInt(remoteCorePoolSize);
            poolingOptions.setCoreConnectionsPerHost(HostDistance.REMOTE, iremoteCorePoolSize);
        }
        if (!StringUtils.trimToEmpty(remoteMaxPoolSize).isEmpty() && StringUtil.isNullOrEmpty(remoteMaxPoolSize)) {
            remoteMaxPoolSize = StringUtils.trim(remoteMaxPoolSize);
            int iremoteMaxPoolSize = Integer.parseInt(remoteMaxPoolSize);
            poolingOptions.setMaxConnectionsPerHost(HostDistance.REMOTE, iremoteMaxPoolSize);
        }
        Cluster.Builder builder = new Cluster.Builder()
                .addContactPoints(server1, server2)
                .withPort(port).withLoadBalancingPolicy(new TokenAwarePolicy(roundRobinPolicy, true))
                .withPoolingOptions(poolingOptions)
                .withCredentials(userid, password);
        if (useSSL) {
            builder.withSSL();
        }
        cluster = builder.build();
        this.session = cluster.connect(keyspace);
        Metadata metadata = cluster.getMetadata();
        logger.info(String.format("Connected to cluster: %s\n", metadata.getClusterName()));
        for (Host host : metadata.getAllHosts()) {
            logger.info(String.format("Datacenter: %s; Host: %s; Rack: %s\n", host.getDatacenter(), host.getAddress(), host.getRack()));
        }
    }

    public void destroy() {
        logger.info("Closing connections to Cassandra");
        this.session.close();
        this.cluster.close();
    }

    public boolean refreshSession() {
        //return true if able to connect to cassandra
        try {
            ResultSetFuture future = this.session.executeAsync(testQuery);
            future.getUninterruptibly(REFRESH_RESPONSE_TIMEOUT, TimeUnit.MILLISECONDS);
            logger.info("Successfully connected to DB. Session refreshed");
            return true;
        } catch (Exception e) {
            logger.info("Still not able to connect to DB. Error " + e);
            return false;
        }
    }

    public ResultSet execute(BoundStatement bstmt) throws NoHostAvailableException, QueryExecutionException, QueryValidationException, UnsupportedFeatureException {
        if (this.profilingDBCalls) {
            dbProfileLogger.info(String.format("%s", bstmt.preparedStatement().getQueryString()));
        }
        return this.session.execute(bstmt);
    }

    public ResultSetFuture executeAsynchronous(BoundStatement bsmt) throws UnsupportedFeatureException {
        if (this.profilingDBCalls) {
            dbProfileLogger.info(String.format("%s", bsmt.preparedStatement().getQueryString()));
        }
        return this.session.executeAsync(bsmt);
    }


}

