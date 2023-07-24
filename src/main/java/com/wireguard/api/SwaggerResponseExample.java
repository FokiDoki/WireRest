package com.wireguard.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public enum SwaggerResponseExample {

    BAD_REQUEST(getJsonFromObject(new AppError(400, "publicKey.value: Key must be 44 characters long (null provided)")));


    private String jsonExample;


    SwaggerResponseExample(String jsonExample) {
        this.jsonExample = jsonExample;
    }

    public String getJson() {
        return jsonExample;
    }



    private static String getJsonFromObject(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }



}
