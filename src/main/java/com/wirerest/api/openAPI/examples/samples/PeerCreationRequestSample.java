package com.wirerest.api.openAPI.examples.samples;

import com.wirerest.api.openAPI.schemas.samples.PeerCreationRequestSchema;

import java.util.Set;

public class PeerCreationRequestSample extends PeerCreationRequestSchema {
    public PeerCreationRequestSample() {
        super();
        setPrivateKey("wGzxmo5PiRGwH4e2SkahWzGetwbrk7NZ3Pcj16hKJWk=");
        setPresharedKey("KEeLDNgckUkGDsomn6Q7UQvM409BRGLNmnG1w0Y+cVQ=");
        setPublicKey("qHnbCAkk9xn3NqZ3lnC3TD7DwjnhTGmE8BrWd+9QPEo=");
        setAllowedIps(Set.of("10.0.0.10/32"));
        setPersistentKeepalive(0);
    }
}
