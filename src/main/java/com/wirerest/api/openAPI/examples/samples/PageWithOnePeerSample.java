package com.wirerest.api.openAPI.examples.samples;

import com.wirerest.api.dto.PageDTO;

import java.util.List;

public class PageWithOnePeerSample extends PageDTO<PeerDTOSample> {
    public PageWithOnePeerSample() {
        super(100, 0, List.of(new PeerDTOSample()));
    }
}
