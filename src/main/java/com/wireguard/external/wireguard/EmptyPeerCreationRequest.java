package com.wireguard.external.wireguard;

import com.wireguard.external.network.Subnet;

import java.util.Set;

public class EmptyPeerCreationRequest extends PeerCreationRequest{
    public EmptyPeerCreationRequest() {
        super(null, null, null, null, null);
    }
}
