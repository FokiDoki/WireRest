package com.wirerest.wireguard.peer.requests;

import com.wirerest.network.ISubnet;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class PeerUpdateRequest {
    private final String currentPublicKey;
    private final String newPublicKey;
    private final String presharedKey;
    private final Set<ISubnet> allowedIps;
    private final String endpoint;
    private final Integer persistentKeepalive;

}
