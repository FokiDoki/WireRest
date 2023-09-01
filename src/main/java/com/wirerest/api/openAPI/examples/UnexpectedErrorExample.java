package com.wirerest.api.openAPI.examples;

import com.wirerest.api.openAPI.examples.samples.exceptions.SampleUnexpectedError;
import org.springframework.stereotype.Component;

@Component
public class UnexpectedErrorExample extends IdentifiedExample{
    public UnexpectedErrorExample() {
        super("UnexpectedError500");
        summary("Unexpected error");
        setValue(new SampleUnexpectedError());
    }
}
