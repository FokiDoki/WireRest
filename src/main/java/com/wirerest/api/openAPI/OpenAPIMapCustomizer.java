package com.wirerest.api.openAPI;

import java.util.HashMap;
import java.util.Map;

public class OpenAPIMapCustomizer<String, T> {
    private final Map<String, T> map = new HashMap<>();

    public void put(String key, T value) {
        map.put(key, value);
    }

    public Map<String, T> getMap() {
        return map;
    }

}
