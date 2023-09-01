package com.wirerest.api.openAPI.examples;

import com.wirerest.api.openAPI.examples.samples.exceptions.SampleInvalidLogsLimit;
import org.springframework.stereotype.Component;

@Component
public class InvalidLogLimitExample extends IdentifiedExample{

    public InvalidLogLimitExample() {
        super("logsLimit400");
        summary("Invalid limit");
        setValue(new SampleInvalidLogsLimit());
    }

}
