package com.wireguard.api.peer;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@Data
public class PeerCreationRequestDTO {
    @Nullable
    private final String publicKey;
    @Nullable
    private final String presharedKey;
    @Nullable
    private final String privateKey;
    @Nullable
    private final Set<String> allowedIps;
    @Nullable
    @Min(0)
    @Max(65535)
    private final Integer persistentKeepalive;

    public Set<String> getAllowedIps() {
        if (allowedIps == null) {
            return new HashSet<>();
        }
        return allowedIps;
    }
}
