package com.wirerest.api.inteface;

import com.wirerest.wireguard.iface.WgInterface;
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

    public static WgInterfaceDTO from(WgInterface wgInterface) {
        return new WgInterfaceDTO(
                wgInterface.getPrivateKey(),
                wgInterface.getPublicKey(),
                wgInterface.getListenPort(),
                wgInterface.getFwMark()
        );
    }
}
