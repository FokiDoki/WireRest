package com.wirerest.api.openAPI;

import io.swagger.v3.oas.models.media.Schema;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class OpenAPISchemasConfigurator {

    @Autowired
    private List<? extends Schema> schemas;



    @Bean
    public OpenApiCustomizer applySchemas() {
        return openApi -> {
            System.out.println("Schemas configured");
            Map<String, Schema> schemasMap = new HashMap<>(openApi.getComponents().getSchemas());
            schemas.forEach(scheme -> schemasMap.put("PeerCreationRequestSchema", scheme));
            openApi.getComponents().setSchemas(schemasMap);

        };
    }
}