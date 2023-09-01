package com.wirerest.api.openAPI.examples;

import com.wirerest.api.openAPI.examples.samples.ExamplePageWithOnePeer;
import org.springframework.stereotype.Component;

@Component
public class PageWithPeersExample extends IdentifiedExample{
    public PageWithPeersExample() {
        super("PageWithPeers");
        summary("Page with limit 1");
        setValue(new ExamplePageWithOnePeer());
    }
}
