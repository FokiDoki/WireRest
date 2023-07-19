package com.wireguard.api.peer;

import com.wireguard.api.dto.PublicKey;
import com.wireguard.external.wireguard.PeerCreationRequest;
import com.wireguard.utils.IpUtils;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@AllArgsConstructor
@Data
public class PeerCreationRequestDTO {
    @Nullable
    private final PublicKey publicKey;
    @Nullable
    @Pattern(regexp = "^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$", message = "Not a base64 encoded preshared key")
    private final String presharedKey;
    @Nullable
    @Pattern(regexp = "^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$", message = "Not a base64 encoded private key")
    private final String privateKey;
    @Nullable
    private final Set<String> allowedIps;
    @Nullable
    @Min(0)
    @Max(65535)
    private final Integer persistentKeepalive;



}
