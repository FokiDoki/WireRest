package com.wireguard.api.inteface;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WgInterfaceDTO {
    private final String privateKey;
    private final String publicKey;
    @Min(1)
    @Max(65535)
    private final int listenPort;
    private final int fwMark;


}
