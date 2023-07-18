package com.wireguard.api.peer;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class PeerUpdateRequestDTO {
    @NotNull
    private final String publicKey;
    private final String newPublicKey;
    private final String presharedKey;
    private final Set<String> allowedIps;
    private final String endpoint;
    private final Integer persistentKeepalive;
}
