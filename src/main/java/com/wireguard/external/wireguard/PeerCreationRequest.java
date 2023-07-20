package com.wireguard.external.wireguard;

import com.wireguard.external.network.ISubnet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@AllArgsConstructor
@Data
@EqualsAndHashCode
public class PeerCreationRequest {
    private final String publicKey;
    private final String presharedKey;
    private final String privateKey;
    private Set<ISubnet> allowedIps;
    private final Integer persistentKeepalive;


}
