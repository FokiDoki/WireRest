package com.wireguard.external.wireguard;

import com.wireguard.external.network.Subnet;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Data
public class PeerCreationRequest {
    private final String publicKey;
    private final String presharedKey;
    private final String privateKey;
    private final Set<Subnet> allowedIps;
    private final Integer persistentKeepalive;
}
