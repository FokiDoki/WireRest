package com.wireguard.external.wireguard.iface;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WgInterface {
    private String privateKey;
    private String publicKey;
    private int listenPort;
    private int fwMark;
}
