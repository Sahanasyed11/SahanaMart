package com.sahana.sahanamart.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.InputStream;
import java.io.Reader;

/**
 * Thread-safe JSON serialization/deserialization utility using Jackson.
 */
public class JsonUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static ObjectMapper getMapper() {
        return mapper;
    }

    public static String toJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize to JSON: " + e.getMessage(), e);
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize from JSON: " + e.getMessage(), e);
        }
    }

    public static <T> T fromJson(InputStream is, Class<T> clazz) {
        try {
            return mapper.readValue(is, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize stream from JSON: " + e.getMessage(), e);
        }
    }

    public static <T> T fromJson(Reader reader, Class<T> clazz) {
        try {
            return mapper.readValue(reader, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize reader from JSON: " + e.getMessage(), e);
        }
    }
}
