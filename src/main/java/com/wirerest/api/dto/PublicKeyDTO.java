package com.wirerest.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class PublicKeyDTO {
    @Valid
    @Getter
    @NotNull
    private final RequiredWgKey publicKey;

    public String getValue() {
        return publicKey.getValue();
    }
}
