package com.wirerest.wireguard.peer.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@Data
@EqualsAndHashCode
public class PeerCreationRequest {
    private final String publicKey;
    private final String presharedKey;
    private final String privateKey;
    private final IpAllocationRequest ipAllocationRequest;
    private final Integer persistentKeepalive;
}
