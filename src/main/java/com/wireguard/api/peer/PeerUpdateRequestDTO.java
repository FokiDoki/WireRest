package com.wireguard.api.peer;

import com.wireguard.api.dto.RequiredWgKey;
import com.wireguard.api.dto.Socket;
import com.wireguard.api.dto.WgKey;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
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
    @Valid
    private final RequiredWgKey publicKey;
    @Nullable
    @Valid
    private final RequiredWgKey newPublicKey;
    @Nullable
    @Valid
    private final WgKey presharedKey;
    private final Set<String> allowedIps;
    @Valid
    private final Socket endpoint;
    @Min(0)
    @Max(65535)
    private final Integer persistentKeepalive;
}

