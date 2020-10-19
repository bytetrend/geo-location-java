package io.bytetrend.geo.location;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class PointOfInterestLocatorApplication {

//https://grokonez.com/spring-framework/spring-integration/use-spring-integration-http-inbound-spring-boot
//https://docs.spring.io/spring-integration/docs/5.1.3.RELEASE/reference/html/#amqp-inbound-gateway

    public static void main(String[] args) {
        SpringApplication.run(PointOfInterestLocatorApplication.class, args);
    }

}
