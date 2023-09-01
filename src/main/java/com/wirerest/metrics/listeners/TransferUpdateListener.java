package com.wirerest.metrics.listeners;

import com.wirerest.metrics.SyncMetricsService;
import com.wirerest.wireguard.events.SyncTransferUpdatedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "wg.cache.enabled", havingValue = "true")
public class TransferUpdateListener implements ApplicationListener<SyncTransferUpdatedEvent> {
    private final SyncMetricsService metricsService;

    @Autowired
    public TransferUpdateListener(SyncMetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @Override
    public void onApplicationEvent(SyncTransferUpdatedEvent event) {
        metricsService.metrics.transferTxTotal.set(event.getTransfer().getTx());
        metricsService.metrics.transferRxTotal.set(event.getTransfer().getRx());

    }
}
