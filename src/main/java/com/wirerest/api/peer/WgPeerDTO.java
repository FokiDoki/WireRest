package com.wirerest.api.peer;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
