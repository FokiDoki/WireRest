package com.wirerest.metrics;

import com.wirerest.network.IV4SubnetSolver;
import com.wirerest.wireguard.peer.WgPeer;
import com.wirerest.wireguard.peer.WgPeerService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@ConditionalOnProperty(value = "wg.cache.enabled", havingValue = "false")
public class MetricsService implements IMetricsService{

    IV4SubnetSolver subnetSolver;
    WgPeerService wgPeerService;

    public MetricsService(IV4SubnetSolver subnetSolver, WgPeerService wgPeerService) {
        this.subnetSolver = subnetSolver;
        this.wgPeerService = wgPeerService;
    }

    public StatsSnapshot snapshot() {
        List<WgPeer> peers = wgPeerService.getPeers();
        return new StatsSnapshot.Builder()
                .timestamp(Instant.now())
                .freeV4Ips(subnetSolver.getAvailableIpsCount())
                .totalV4Ips(subnetSolver.getTotalIpsCount())
                .totalPeers(peers.size())
                .transferRxTotal(peers.stream().mapToLong(WgPeer::getTransferRx).sum())
                .transferTxTotal(peers.stream().mapToLong(WgPeer::getTransferTx).sum())
                .build();
    }
}
