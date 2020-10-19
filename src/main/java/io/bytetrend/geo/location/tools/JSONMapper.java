package io.bytetrend.geo.location.tools;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.math.BigInteger;

public final class JSONMapper {

    private final static ObjectMapper jsonMapper = new ObjectMapper();

    static {
        jsonMapper.registerModule(new JavaTimeModule());
        jsonMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        jsonMapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        jsonMapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        SimpleModule module = new SimpleModule();
        module.addSerializer(BigInteger.class, new ToStringSerializer());
        jsonMapper.registerModule(module);
        jsonMapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        jsonMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public static String getJsonFromBean(Object bean) throws IOException {
        return jsonMapper.writeValueAsString(bean);
    }

    public static <T> T getBeanFromJson(String json, Class<T> type) throws IOException {
        return jsonMapper.readValue(json, type);
    }
}
