package com.wirerest.metrics;

import com.wirerest.network.IV4SubnetSolver;
import com.wirerest.wireguard.peer.WgPeer;
import com.wirerest.wireguard.peer.WgPeerService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@ConditionalOnProperty(value = "wg.cache.enabled", havingValue = "true")
public class SyncMetricsService {

    IV4SubnetSolver subnetSolver;
    WgPeerService wgPeerService;

    public WireRestMetrics metrics = new WireRestMetrics();

    public SyncMetricsService(IV4SubnetSolver subnetSolver, WgPeerService wgPeerService) {
        this.subnetSolver = subnetSolver;
        this.wgPeerService = wgPeerService;
        metrics.freeV4Ips.set(subnetSolver.getAvailableIpsCount());
        metrics.totalV4Ips.set(subnetSolver.getTotalIpsCount());
        List<WgPeer> peers = wgPeerService.getPeers();
        metrics.totalPeers.set(peers.size());
        Utils.Transfer transfer = new Utils().calculateTransfer(peers);
        metrics.transferTxTotal.set(transfer.getTx());
        metrics.transferRxTotal.set(transfer.getRx());

    }

    public StatsSnapshot snapshot() {
        return new StatsSnapshot.Builder()
                .timestamp(Instant.now())
                .freeV4Ips(metrics.freeV4Ips.get())
                .totalV4Ips(metrics.totalV4Ips.get())
                .totalPeers(metrics.totalPeers.get())
                .transferRxTotal(metrics.transferRxTotal.get())
                .transferTxTotal(metrics.transferTxTotal.get())
                .build();
    }
}
