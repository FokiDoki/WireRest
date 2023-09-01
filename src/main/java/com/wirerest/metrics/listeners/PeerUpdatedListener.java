package com.wirerest.metrics.listeners;

import com.wirerest.metrics.SyncMetricsService;
import com.wirerest.wireguard.events.PeerUpdatedEvent;
import com.wirerest.wireguard.peer.WgPeer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "wg.cache.enabled", havingValue = "true")
public class PeerUpdatedListener implements ApplicationListener<PeerUpdatedEvent> {
    private final SyncMetricsService metricsService;

    @Autowired
    public PeerUpdatedListener(SyncMetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @Override
    public void onApplicationEvent(PeerUpdatedEvent event) {
        WgPeer oldPeer = event.getOldPeer();
        WgPeer newPeer = event.getNewPeer();
        metricsService.metrics.freeV4Ips.add(
                newPeer.getAllowedSubnets().getIPv4Subnets().size() -
                        oldPeer.getAllowedSubnets().getIPv4Subnets().size()
        );

    }
}
