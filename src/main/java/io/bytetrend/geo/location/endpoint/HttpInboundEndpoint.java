package io.bytetrend.geo.location.endpoint;


import io.bytetrend.geo.location.model.PointOfInterest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class HttpInboundEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpInboundEndpoint.class);

    public Message<?> search(Message<?> msg) {
        LOGGER.info("GET method");
        List<PointOfInterest> custLst = new ArrayList<>();
        return MessageBuilder.withPayload(custLst).copyHeadersIfAbsent(msg.getHeaders())
                .setHeader("http_statusCode", HttpStatus.OK).build();
    }


}
