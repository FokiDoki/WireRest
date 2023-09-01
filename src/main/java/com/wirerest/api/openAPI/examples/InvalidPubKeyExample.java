package com.wirerest.api.openAPI.examples;

import com.wirerest.api.openAPI.examples.samples.exceptions.SampleInvalidPubKey;
import org.springframework.stereotype.Component;

@Component
public class InvalidPubKeyExample extends IdentifiedExample{

    public InvalidPubKeyExample() {
        super("InvalidPubKey400");
        summary("Invalid public key");
        setValue(new SampleInvalidPubKey());
    }

}
