package com.wireguard.api.peer;

import com.wireguard.external.wireguard.peer.WgPeer;
import lombok.*;

import java.util.Set;

@NoArgsConstructor
@Data
@EqualsAndHashCode
public class WgPeerDTO {
    private String publicKey;
    private String presharedKey;
    private String endpoint;
    private Set<String> allowedSubnets;
    private long latestHandshake;
    private long transferRx;
    private long transferTx;
    private int persistentKeepalive;
}
