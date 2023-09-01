package com.wirerest.api.openAPI.examples.samples;

import com.wirerest.api.dto.PageDTO;

import java.util.List;

public class ExamplePageWithOnePeer extends PageDTO<ExamplePeerDTO> {
    public ExamplePageWithOnePeer() {
        super(100, 0, List.of(new ExamplePeerDTO()));
    }
}
