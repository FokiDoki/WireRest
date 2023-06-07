package com.wireguard.external.wireguard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreatedPeer {
    private String publicKey;
    private String presharedKey;
    private String privateKey;
    private String address;
    private int persistentKeepalive;
}
