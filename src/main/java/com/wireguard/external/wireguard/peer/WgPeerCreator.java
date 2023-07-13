package com.wireguard.external.wireguard.peer;

import com.wireguard.external.network.AlreadyUsedException;
import com.wireguard.external.network.ISubnetSolver;
import com.wireguard.external.network.NetworkInterfaceDTO;
import com.wireguard.external.network.Subnet;
import com.wireguard.external.shell.CommandExecutionException;
import com.wireguard.external.shell.ShellRunner;
import com.wireguard.external.wireguard.PeerCreationRequest;
import com.wireguard.external.wireguard.WgTool;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class WgPeerCreator {

    private static final Logger logger = LoggerFactory.getLogger(ShellRunner.class);
    @Value("${wg.interface.default.mask}")
    private final int DEFAULT_MASK_FOR_NEW_CLIENTS = 32;
    @Value("${wg.interface.default.persistent_keepalive}")
    public final int DEFAULT_PERSISTENT_KEEPALIVE = 0;

    private final ISubnetSolver wgSubnetSolver;
    private final WgTool wgTool;

    @Autowired
    public WgPeerCreator(WgTool wgTool, ISubnetSolver wgSubnetSolver) {
        this.wgTool = wgTool;
        this.wgSubnetSolver = wgSubnetSolver;
    }


    public CreatedPeer createPeerGenerateNulls(PeerCreationRequest peerCreationRequest) {
        String privateKey = peerCreationRequest.getPrivateKey() == null ? wgTool.generatePrivateKey() : peerCreationRequest.getPrivateKey();
        String publicKey = peerCreationRequest.getPublicKey() == null ? wgTool.generatePublicKey(privateKey) : peerCreationRequest.getPublicKey();
        String presharedKey = peerCreationRequest.getPresharedKey() == null ? wgTool.generatePresharedKey() : peerCreationRequest.getPresharedKey();
        int persistentKeepalive = peerCreationRequest.getPersistentKeepalive() == null ? DEFAULT_PERSISTENT_KEEPALIVE : peerCreationRequest.getPersistentKeepalive();
        Set<Subnet> allowedIps = peerCreationRequest.getAllowedIps();
        try {
            if (allowedIps != null) {
                allowedIps.forEach(wgSubnetSolver::obtain);
            } else {
                allowedIps = Set.of(wgSubnetSolver.obtainFree(DEFAULT_MASK_FOR_NEW_CLIENTS));
            }
        } catch (AlreadyUsedException e) {
            throw e;
        } catch (Exception e) {
            assert allowedIps != null;
            allowedIps.forEach(wgSubnetSolver::release);
            throw e;
        }
        logger.info("Created peer, public key: %s".formatted(publicKey.substring(0, Math.min(6, publicKey.length()))));
        return new CreatedPeer(publicKey, presharedKey, privateKey, allowedIps, persistentKeepalive);

    }

}
