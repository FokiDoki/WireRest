package com.wireguard.external.wireguard;

import com.wireguard.external.network.Subnet;
import lombok.EqualsAndHashCode;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
public class EmptyPeerCreationRequest extends PeerCreationRequest{
    public EmptyPeerCreationRequest() {
        super(null, null, null, Set.of(), null, 0);
    }
}
