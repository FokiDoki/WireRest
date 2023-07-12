package com.wireguard.external.wireguard.peer;

import com.wireguard.external.network.Subnet;
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
    private Set<Subnet> allowedSubnets;
    private int persistentKeepalive;
}
