package com.wirerest.api.openAPI.examples.samples;


import com.wirerest.api.peer.WgPeerDTO;

import java.util.Set;

public class PeerDTOSample extends WgPeerDTO {
    public PeerDTOSample() {
        super();
        setPublicKey("ALD3x7qWP0W/4zC26jFozxw28vXJsazA33KnHF+AfHw=");
        setPresharedKey("3hFqZXqzO+YkVL4nX2siavxK1Z3h5lRLkEQz1qf3giI=");
        setEndpoint("123.23.2.3:55412");
        setAllowedSubnets(Set.of("2002:0:0:1234::/64", "10.1.142.196/32"));
        setLatestHandshake(1690200786);
        setTransferRx(12345);
        setTransferTx(54321);
        setPersistentKeepalive(25);
    }
}
