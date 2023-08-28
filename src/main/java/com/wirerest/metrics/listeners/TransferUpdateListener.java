package com.wirerest.metrics.listeners;

import com.wirerest.metrics.MetricsService;
import com.wirerest.wireguard.events.SyncTransferUpdatedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class TransferUpdateListener implements ApplicationListener<SyncTransferUpdatedEvent> {
    private final MetricsService metricsService;

    @Autowired
    public TransferUpdateListener(MetricsService metricsService) {
        this.metricsService = metricsService;
    }
    
    @Override
    public void onApplicationEvent(SyncTransferUpdatedEvent event) {
        metricsService.metrics.transferTxTotal.set(event.getTransfer().getTx());
        metricsService.metrics.transferRxTotal.set(event.getTransfer().getRx());

    }
}
