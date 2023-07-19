package com.wireguard.api.peer;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.checkerframework.checker.regex.qual.Regex;
import org.checkerframework.common.value.qual.MatchesRegex;

import java.util.Set;

@Data
@AllArgsConstructor
public class PeerUpdateRequestDTO {
    @NotNull
    @Pattern(regexp = "^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$", message = "Not a base64 encoded public key")
    private final String publicKey;
    @Nullable
    @Pattern(regexp = "^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$", message = "Not a base64 encoded public key")
    private final String newPublicKey;
    @Pattern(regexp = "^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$", message = "Not a base64 encoded preshared key")
    private final String presharedKey;
    private final Set<String> allowedIps;
    private final String endpoint;
    @Min(0)
    @Max(65535)
    private final Integer persistentKeepalive;
}
