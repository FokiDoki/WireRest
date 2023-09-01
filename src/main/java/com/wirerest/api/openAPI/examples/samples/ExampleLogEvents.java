package com.wirerest.api.openAPI.examples.samples;

import com.wirerest.logs.LoggingEventDto;

import java.util.ArrayList;

public class ExampleLogEvents extends ArrayList<LoggingEventDto> {
    public ExampleLogEvents() {
        super();
        add(new LoggingEventDto("INFO", "Init duration for springdoc-openapi is: 529 ms", 1690301255231L));
        add(new LoggingEventDto("ERROR", "Peer with public key ALD3x7qWP0W/4zC26jFozxw28vXJsazA33KnHF+AfHw= not found", 1690301333239L));
    }
}
