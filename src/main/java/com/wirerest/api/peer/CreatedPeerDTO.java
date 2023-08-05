package com.wirerest.api.peer;

import com.wirerest.wireguard.peer.CreatedPeer;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class CreatedPeerDTO {
    private final String publicKey;
    private final String presharedKey;
    private final String privateKey;
    private final Set<String> allowedSubnets;
    private final int persistentKeepalive;

    public static CreatedPeerDTO from(CreatedPeer createdPeer) {
        return new CreatedPeerDTO(
                createdPeer.getPublicKey(),
                createdPeer.getPresharedKey(),
                createdPeer.getPrivateKey(),
                createdPeer.getAllowedSubnets().stream().map(String::valueOf).collect(Collectors.toSet()),
                createdPeer.getPersistentKeepalive()
        );
    }
}
