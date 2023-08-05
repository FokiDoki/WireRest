package com.wirerest.api.peer;

import com.wirerest.api.dto.RequiredWgKey;
import com.wirerest.api.dto.Socket;
import com.wirerest.api.dto.WgKey;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

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

