package com.wirerest.api.openAPI.examples;

import com.wirerest.api.openAPI.examples.samples.ExampleCreatedPeerDTO;
import org.springframework.stereotype.Component;

@Component
public class CreatedPeerExample extends IdentifiedExample{
    public CreatedPeerExample() {
        super("createdPeer");
        summary("Created peer");
        setValue(new ExampleCreatedPeerDTO());
    }
}
