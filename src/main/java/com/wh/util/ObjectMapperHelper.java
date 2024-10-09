package com.wh.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperHelper {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> String convertToJson(T objectToConvert) {
        try {
            return objectMapper.writeValueAsString(objectToConvert);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    public static <T> T convertFromJson(String jsonObject, Class<T> targetClass) {
        try {
            return objectMapper.readValue(jsonObject, targetClass);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}