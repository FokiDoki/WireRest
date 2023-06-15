package com.wireguard.external.wireguard.dto;

import com.sun.source.tree.Tree;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.Set;
import java.util.TreeSet;

@Getter
@AllArgsConstructor
public class CreatedPeer {
    private String publicKey;
    private String presharedKey;
    private String privateKey;
    private Set<String> address;
    private int persistentKeepalive;
}
