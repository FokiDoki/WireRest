package com.wirerest.api.openAPI.examples;

import com.wirerest.api.openAPI.examples.samples.exceptions.SampleAlreadyUsed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AlreadyUsedExample extends IdentifiedExample{

    @Autowired
    public AlreadyUsedExample(SampleAlreadyUsed alreadyUsedSample) {
        super("alreadyUsed409");
        summary("Already Used");
        setValue(alreadyUsedSample);
    }
}
