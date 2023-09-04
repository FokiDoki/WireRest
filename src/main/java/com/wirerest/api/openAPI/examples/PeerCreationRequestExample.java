package com.wirerest.api.openAPI.examples;

import com.wirerest.api.openAPI.examples.samples.PeerCreationRequestSample;
import org.springframework.stereotype.Component;

@Component
public class PeerCreationRequestExample extends IdentifiedExample{

    public PeerCreationRequestExample() {
        super("PeerCreationRequestSchema");
        setValue(new PeerCreationRequestSample());
    }

}
