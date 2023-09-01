package com.wirerest.api.openAPI.examples;

import com.wirerest.api.openAPI.examples.samples.exceptions.SamplePeerNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PeerNotFoundExample extends IdentifiedExample{

    @Autowired
    public PeerNotFoundExample(SamplePeerNotFound notFoundExample) {
        super("peerNotFound");
        summary("Peer not found");
        setValue(notFoundExample);
    }
}
