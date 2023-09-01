package com.wirerest.api.openAPI;

import com.wirerest.api.AppError;
import com.wirerest.api.openAPI.examples.DefaultIdentifiedExample;
import com.wirerest.api.openAPI.examples.IdentifiedExample;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Configuration
public class OpenAPIExamplesConfigurator {

    @Autowired
    private List<? extends IdentifiedExample> examples;

    @Autowired
    private List<? extends DefaultIdentifiedExample> defaultExamples;


    @Bean
    public OpenApiCustomizer applyExamples() {
        return openApi -> {
            Map<String, Example> examplesMap = new HashMap<>();
            examples.forEach(example -> examplesMap.put(example.getKey(), example));
            openApi.getComponents().setExamples(examplesMap);
        };
    }

    @Bean
    public OpenApiCustomizer applyDefaultErrorExamples() {
        return openApi -> {
            Map<String, Schema> schemas = openApi.getComponents().getSchemas();
            openApi.getPaths().values().forEach(pathItem -> pathItem.readOperations().forEach(operation -> {
                ApiResponses apiResponses = operation.getResponses();
                for (IdentifiedExample identifiedExample: defaultExamples){
                    AppError error = (AppError) identifiedExample.getValue();
                    addApiResponseNoOverride(apiResponses, String.valueOf(error.getCode()),
                            createApiResponse(identifiedExample.getSummary(),
                                    schemas.get(AppError.class.getSimpleName()),
                                    Map.of(identifiedExample.getKey(), identifiedExample)
                            ));
                }

            }));
        };
    }

    private void addApiResponseNoOverride(ApiResponses apiResponses, String code, ApiResponse apiResponse) {
        if (apiResponses.containsKey(code)) {
            MediaType newMediaType = apiResponse.getContent().get(org.springframework.http.MediaType.APPLICATION_JSON_VALUE);
            MediaType oldMediaType = apiResponses.get(code).getContent().get(org.springframework.http.MediaType.APPLICATION_JSON_VALUE);
            Map<String, Example> oldExamples = Objects.requireNonNullElse(
                    oldMediaType.getExamples(),
                    new HashMap<>());
            Map<String, Example> newExamples = Objects.requireNonNullElse(
                    newMediaType.getExamples(),
                    new HashMap<>());
            oldExamples.putAll(newExamples);
            setExamples(apiResponse, oldExamples);
        }
        apiResponses.addApiResponse(code, apiResponse);
    }

    private ApiResponse createApiResponse(String message, Schema schema, Map<String, Example> example) {
        ApiResponse apiResponse = createApiResponse(message, schema);
        setExamples(apiResponse, example);
        return apiResponse;
    }

    private ApiResponse createApiResponse(String message, Schema schema) {
        MediaType mediaType = new MediaType();
        mediaType.schema(schema);
        return new ApiResponse().description(message)
                .content(new Content().addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE, mediaType));
    }

    private void setExamples(ApiResponse apiResponse, Map<String, Example> examples) {
        apiResponse.getContent()
                .get(org.springframework.http.MediaType.APPLICATION_JSON_VALUE).setExamples(examples);
    }
}