package com.wirerest.metrics.actuator;

import com.wirerest.metrics.IMetricsService;
import com.wirerest.metrics.WireRestMetrics;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicLong;


@Component
@Scope("singleton")
public class MetricsToActuatorExporter {

    private final MeterRegistry registry;
    private final IMetricsService metricsService;

    private final ScheduledExecutorService metricsUpdateScheduler = Executors.newSingleThreadScheduledExecutor();

    private WireRestMetrics metrics;
    private AtomicLong lastTotalPeers = new AtomicLong();

    @Autowired
    public MetricsToActuatorExporter(IMetricsService metricsService, MeterRegistry registry) {
        this.registry = registry;
        this.metricsService = metricsService;

        metricsUpdateScheduler.scheduleAtFixedRate(this::updateMetrics,
                2, 1, java.util.concurrent.TimeUnit.SECONDS);
    }

    private void updateMetrics(){
        metrics = metricsService.getMetrics();
        Gauge.builder("wirerest_net_transfer", metrics, metrics -> metrics.transferTxTotal.get())
                        .tag("type", "transfer").register(registry);
        Gauge.builder("wirerest_net_transfer", metrics, metrics -> metrics.transferRxTotal.get())
                .tag("type", "receive").register(registry);
        registry.gauge("wirerest_peers_total", metrics, metrics -> metrics.totalPeers.get());
        registry.gauge("wirerest_iface_ipv4_free",  metrics, metrics -> metrics.freeV4Ips.get());
        registry.gauge("wirerest_iface_ipv4_total",  metrics, metrics -> metrics.totalV4Ips.get());

    }



}