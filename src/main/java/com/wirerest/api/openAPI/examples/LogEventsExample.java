package com.wirerest.api.openAPI.examples;

import com.wirerest.api.openAPI.examples.samples.LogEventsSample;
import org.springframework.stereotype.Component;

@Component
public class LogEventsExample extends IdentifiedExample{
    public LogEventsExample() {
        super("logs");
        summary("Logs");
        setValue(new LogEventsSample());
    }
}
