package com.wirerest.api.openAPI.examples;

import com.wirerest.api.openAPI.examples.samples.exceptions.SampleInvalidPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InvalidPageExample extends IdentifiedExample{

    @Autowired
    public InvalidPageExample(SampleInvalidPage sampleInvalidPage) {
        super("InvalidPage400");
        summary("Invalid Page");
        setValue(sampleInvalidPage);
    }
}
