package com.wireguard.external.wireguard.peer;

import com.wireguard.external.network.ISubnetSolver;
import com.wireguard.external.network.NetworkInterfaceDTO;
import com.wireguard.external.network.Subnet;
import com.wireguard.external.shell.CommandExecutionException;
import com.wireguard.external.shell.ShellRunner;
import com.wireguard.external.wireguard.WgTool;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class WgPeerCreator {

    private static final Logger logger = LoggerFactory.getLogger(ShellRunner.class);
    @Value("${wg.interface.default.mask}")
    private final int DEFAULT_MASK_FOR_NEW_CLIENTS = 32;
    @Value("${wg.interface.default.persistent_keepalive}")
    private final int DEFAULT_PERSISTENT_KEEPALIVE = 0;

    private final ISubnetSolver wgSubnetSolver;
    private final WgTool wgTool;

    @Autowired
    public WgPeerCreator(WgTool wgTool, ISubnetSolver wgSubnetSolver, NetworkInterfaceDTO wgInterface) {
        this.wgTool = wgTool;
        this.wgSubnetSolver = wgSubnetSolver;
    }

    private CreatedPeer createPeer(String privateKey, String publicKey, String presharedKey, Set<Subnet> allowedIps, int persistentKeepalive) {
        CreatedPeer createdPeer = new CreatedPeer(publicKey, presharedKey, privateKey,
                allowedIps,
                persistentKeepalive);
        logger.info("Created peer, public key: %s".formatted(publicKey.substring(0, 6)));
        return createdPeer;
    }

    public CreatedPeer createPeerGenerateNulls(@Nullable String publicKey, @Nullable String presharedKey,
                                               @Nullable String privateKey, @Nullable Set<Subnet> allowedIps,
                                               @Nullable Integer persistentKeepalive){
        privateKey = privateKey == null ? wgTool.generatePrivateKey() : privateKey;
        publicKey = publicKey == null ? wgTool.generatePublicKey(privateKey) : publicKey;
        presharedKey = presharedKey == null ? wgTool.generatePresharedKey() : presharedKey;
        persistentKeepalive = persistentKeepalive == null ? DEFAULT_PERSISTENT_KEEPALIVE : persistentKeepalive;
        if (allowedIps != null) {
            allowedIps.forEach(wgSubnetSolver::obtain);
        } else {
            allowedIps = Set.of(wgSubnetSolver.obtainFree(DEFAULT_MASK_FOR_NEW_CLIENTS));
        }
        try {
            return createPeer(privateKey, publicKey, presharedKey, allowedIps, persistentKeepalive);
        } catch (CommandExecutionException e) {
            allowedIps.forEach(wgSubnetSolver::release);
            throw e;
        }
    }

}
