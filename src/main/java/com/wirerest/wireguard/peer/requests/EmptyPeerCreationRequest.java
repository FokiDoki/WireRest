package com.wirerest.wireguard.peer.requests;

import lombok.EqualsAndHashCode;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
public class EmptyPeerCreationRequest extends PeerCreationRequest {
    public EmptyPeerCreationRequest() {
        super(null, null, null,
                new IpAllocationRequestOneIfNullSubnets(Set.of()), null);
    }
}
