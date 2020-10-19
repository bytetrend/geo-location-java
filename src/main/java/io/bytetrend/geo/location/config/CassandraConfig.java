package io.bytetrend.geo.location.config;

import io.bytetrend.geo.location.sink.connector.CassandraConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CassandraConfig {

    @Value("${spring.data.cassandra.cluster.contact-points}")
    private String contactPoints;
    @Value("${spring.data.cassandra.cluster.port}")
    private int port;
    @Value("${spring.data.cassandra.cluster.keyspace-name}")
    private String keyspaceName;
    @Value("${spring.data.cassandra.cluster.dc}")
    private String dc;

    @Value("${spring.data.cassandra.cluster.user-id}")
    private String userId;
    @Value("${spring.data.cassandra.cluster.password}")
    private String password;
    @Value("${spring.data.cassandra.cluster.use-ssl}")
    private boolean useSSL;

    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraConfig.class);

    @Bean
    public CassandraConnector cassandraDao() {
        String[] brokers = contactPoints.split(",");
        return new CassandraConnector(brokers[0], brokers.length > 1 ? brokers[1] : brokers[0],
                port, keyspaceName, dc, userId, password, useSSL);
    }
}
