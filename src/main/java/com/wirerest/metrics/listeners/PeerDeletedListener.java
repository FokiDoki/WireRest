package com.wirerest.metrics.listeners;

import com.wirerest.metrics.SyncMetricsService;
import com.wirerest.wireguard.events.PeerDeletedEvent;
import com.wirerest.wireguard.peer.WgPeer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class PeerDeletedListener implements ApplicationListener<PeerDeletedEvent> {
    private final SyncMetricsService metricsService;
    
    @Autowired
    public PeerDeletedListener(SyncMetricsService metricsService) {
        this.metricsService = metricsService;
    }
    
    @Override
    public void onApplicationEvent(PeerDeletedEvent event) {
        WgPeer deletedPeer = event.getPeer();
        metricsService.metrics.totalPeers.decrement();
        metricsService.metrics.freeV4Ips.add(
                deletedPeer.getAllowedSubnets().getIPv4Subnets().size());

    }
}
