package com.wireguard.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.validation.annotation.Validated;

@Validated
@Getter
public class RequiredWgKey extends WgKey {

    @Size(min=44, max = 44, message = "Key must be 44 characters long")
    @Pattern(regexp = "^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)$",
            message = "Invalid key format")
    private final String value;

    public RequiredWgKey(String value) {
        super(value);
        this.value = value;
    }
}
