package com.wireguard.external.wireguard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public class CreatedPeer {
    private String publicKey;
    private String presharedKey;
    private String privateKey;
    private Set<String> address;
    private int persistentKeepalive;
}
