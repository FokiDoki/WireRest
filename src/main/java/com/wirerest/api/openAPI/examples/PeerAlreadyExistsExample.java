package com.wirerest.api.openAPI.examples;

import com.wirerest.api.openAPI.examples.samples.exceptions.SamplePeerAlreadyExists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PeerAlreadyExistsExample extends IdentifiedExample{

    @Autowired
    public PeerAlreadyExistsExample(SamplePeerAlreadyExists alreadyExistsExample) {
        super("peerAlreadyExists409");
        summary("Peer already exists");
        setValue(alreadyExistsExample);
    }
}
