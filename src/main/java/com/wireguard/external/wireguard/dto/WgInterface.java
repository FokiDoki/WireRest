package com.wireguard.external.wireguard.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WgInterface {
    private String privateKey;
    private String publicKey;
    @Min(1)
    @Max(65535)
    private int listenPort;
    private int fwmark;


}
