package com.wirerest.api.openAPI.examples;

import io.swagger.v3.oas.models.examples.Example;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IdentifiedExample extends Example {
    private final String key;
}
