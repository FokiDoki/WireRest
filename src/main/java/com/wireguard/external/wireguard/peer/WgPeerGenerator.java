package com.wireguard.external.wireguard.peer;

import com.wireguard.external.network.Subnet;
import com.wireguard.external.wireguard.PeerCreationRequest;
import com.wireguard.external.wireguard.WgTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class WgPeerGenerator {

    private static final Logger logger = LoggerFactory.getLogger(WgPeerGenerator.class);
    @Value("${wg.interface.default.mask}")
    public final int DEFAULT_MASK_FOR_NEW_CLIENTS = 32;
    @Value("${wg.interface.default.persistent_keepalive}")
    public final int DEFAULT_PERSISTENT_KEEPALIVE = 0;

    private final WgTool wgTool;

    @Autowired
    public WgPeerGenerator(WgTool wgTool) {
        this.wgTool = wgTool;
    }


    public CreatedPeer createPeerGenerateNulls(PeerCreationRequest peerCreationRequest) {
        String privateKey = peerCreationRequest.getPrivateKey() == null ? wgTool.generatePrivateKey() : peerCreationRequest.getPrivateKey();
        String publicKey = peerCreationRequest.getPublicKey() == null ? wgTool.generatePublicKey(privateKey) : peerCreationRequest.getPublicKey();
        String presharedKey = peerCreationRequest.getPresharedKey() == null ? wgTool.generatePresharedKey() : peerCreationRequest.getPresharedKey();
        int persistentKeepalive = peerCreationRequest.getPersistentKeepalive() == null ? DEFAULT_PERSISTENT_KEEPALIVE : peerCreationRequest.getPersistentKeepalive();
        Set<Subnet> allowedIps = peerCreationRequest.getAllowedIps();
        logger.info("Created peer, public key: %s".formatted(publicKey.substring(0, Math.min(6, publicKey.length()))));
        return new CreatedPeer(publicKey, presharedKey, privateKey, allowedIps, persistentKeepalive);
    }
}
