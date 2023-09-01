package com.wirerest.metrics;

import com.wirerest.network.IV4SubnetSolver;
import com.wirerest.wireguard.peer.WgPeer;
import com.wirerest.wireguard.peer.WgPeerService;
import lombok.Getter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@ConditionalOnProperty(value = "wg.cache.enabled", havingValue = "false")
public class MetricsService implements IMetricsService {

    IV4SubnetSolver subnetSolver;
    WgPeerService wgPeerService;

    @Getter
    WireRestMetrics metrics = new WireRestMetrics();

    public MetricsService(IV4SubnetSolver subnetSolver, WgPeerService wgPeerService) {
        this.subnetSolver = subnetSolver;
        this.wgPeerService = wgPeerService;
    }

    public StatsSnapshot snapshot() {
        updateMetrics();
        return new StatsSnapshot.Builder()
                .timestamp(Instant.now())
                .freeV4Ips(metrics.freeV4Ips.get())
                .totalV4Ips(metrics.totalV4Ips.get())
                .totalPeers(metrics.totalPeers.get())
                .transferRxTotal(metrics.transferRxTotal.get())
                .transferTxTotal(metrics.transferTxTotal.get())
                .build();
    }

    public void updateMetrics() {
        List<WgPeer> peers = wgPeerService.getPeers();
        metrics.transferTxTotal.set(peers.stream().mapToLong(WgPeer::getTransferTx).sum());
        metrics.transferRxTotal.set(peers.stream().mapToLong(WgPeer::getTransferRx).sum());
        metrics.totalPeers.set(peers.size());
        metrics.totalV4Ips.set(subnetSolver.getTotalIpsCount());
        metrics.freeV4Ips.set(subnetSolver.getAvailableIpsCount());
    }

}
