package com.wireguard.external.wireguard;

import com.wireguard.external.network.Subnet;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class PeerUpdateRequest {
    private final String currentPublicKey;
    private final String NewPublicKey;
    private final String presharedKey;
    private final Set<Subnet> allowedV4Ips;
    private final Set<String> allowedV6Ips;
    private final String endpoint;
    private final Integer persistentKeepalive;

}
