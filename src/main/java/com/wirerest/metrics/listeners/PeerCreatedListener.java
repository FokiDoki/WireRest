package com.wirerest.metrics.listeners;

import com.wirerest.metrics.SyncMetricsService;
import com.wirerest.wireguard.events.PeerCreatedEvent;
import com.wirerest.wireguard.peer.WgPeer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "wg.cache.enabled", havingValue = "true")
public class PeerCreatedListener implements ApplicationListener<PeerCreatedEvent> {
    private final SyncMetricsService metricsService;

    @Autowired
    public PeerCreatedListener(SyncMetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @Override
    public void onApplicationEvent(PeerCreatedEvent event) {
        WgPeer createdPeer = event.getPeer();
        metricsService.metrics.totalPeers.increment();
        metricsService.metrics.freeV4Ips.subtract(
                createdPeer.getAllowedSubnets().getIPv4Subnets().size()
        );
    }
}
