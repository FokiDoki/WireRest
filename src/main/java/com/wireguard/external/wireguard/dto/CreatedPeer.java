package com.wireguard.external.wireguard.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Set;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class CreatedPeer {
    private String publicKey;
    private String presharedKey;
    private String privateKey;
    private Set<String> address;
    private int persistentKeepalive;
}
