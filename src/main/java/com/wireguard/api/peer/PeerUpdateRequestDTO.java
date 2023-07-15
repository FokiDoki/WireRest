package com.wireguard.api.peer;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class PeerUpdateRequestDTO {
    private final String currentPublicKey;
    private final String NewPublicKey;
    private final String presharedKey;
    private final Set<String> allowedIps;
    private final String endpoint;
    private final Integer persistentKeepalive;
}
