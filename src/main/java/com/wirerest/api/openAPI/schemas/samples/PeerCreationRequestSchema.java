package com.wirerest.api.openAPI.schemas.samples;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.util.Set;

@Data
public class PeerCreationRequestSchema {
    @Valid
    private String publicKey;
    @Valid
    @Nullable
    private String presharedKey;
    @Valid
    @Nullable
    private String privateKey;
    @Nullable
    private Set<String> allowedIps;
    @Nullable
    @Min(0)
    @Max(65535)
    private Integer persistentKeepalive;

}
