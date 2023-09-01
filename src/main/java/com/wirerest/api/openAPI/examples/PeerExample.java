package com.wirerest.api.openAPI.examples;

import com.wirerest.api.openAPI.examples.samples.PeerDTOSample;
import org.springframework.stereotype.Component;

@Component
public class PeerExample extends IdentifiedExample{
    public PeerExample() {
        super("peer");
        summary("Example peer");
        setValue(new PeerDTOSample());
    }
}
