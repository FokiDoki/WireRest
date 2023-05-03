package com.wireguard.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WgInterface {
    private String privateKey;
    private String publicKey;
    private int listenPort;
    private int fwmark;


}
