package com.mizuho.trade.instrument.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Created by Dilip on 23/07/2017.
 */
public class JSONUtils {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JSONUtils() {
        objectMapper.setDateFormat(new SimpleDateFormat(PriceConstants.DATE_FORMAT));
    }

    public String mapToJson(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }

    public <T> T mapFromJson(String json, Class<T> clazz) throws JsonParseException, JsonMappingException, IOException {
        return objectMapper.readValue(json, clazz);
    }
}
