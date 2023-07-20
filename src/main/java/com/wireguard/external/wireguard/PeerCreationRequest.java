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
    private final IpAllocationRequest ipAllocationRequest;
    private final Integer persistentKeepalive;
}
