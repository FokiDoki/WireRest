package com.wireguard.external.wireguard.dto;

import com.wireguard.external.wireguard.WgPeer;
import lombok.*;

import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class WgPeerDTO {
    private String publicKey;
    private String presharedKey;
    private String endpoint;
    private Set<String> allowedIps;
    private long latestHandshake;
    private long transferRx;
    private long transferTx;
    private int persistentKeepalive;

    public static WgPeerDTO from(WgPeer wgPeer){
        WgPeerDTO wgPeerDTO = new WgPeerDTO();
        wgPeerDTO.publicKey = wgPeer.getPublicKey();
        wgPeerDTO.presharedKey = wgPeer.getPresharedKey();
        wgPeerDTO.endpoint = wgPeer.getEndpoint();
        wgPeerDTO.allowedIps = wgPeer.getAllowedIps().getAll();
        wgPeerDTO.latestHandshake = wgPeer.getLatestHandshake();
        wgPeerDTO.transferRx = wgPeer.getTransferRx();
        wgPeerDTO.transferTx = wgPeer.getTransferTx();
        wgPeerDTO.persistentKeepalive = wgPeer.getPersistentKeepalive();
        return wgPeerDTO;
    }

}
