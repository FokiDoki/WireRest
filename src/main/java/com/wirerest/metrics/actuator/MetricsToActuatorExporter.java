package com.wirerest.metrics.actuator;

import com.wirerest.metrics.IMetricsService;
import com.wirerest.metrics.MetricsService;
import com.wirerest.metrics.WireRestMetrics;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


@Component
@Scope("singleton")
public class MetricsToActuatorExporter {
    private final IMetricsService metricsService;

    private final WireRestMetrics metrics;

    @Autowired
    public MetricsToActuatorExporter(IMetricsService metricsService, MeterRegistry registry) {
        this.metricsService = metricsService;
        this.metrics = metricsService.getMetrics();
        buildMetrics(registry);
        if (metricsService instanceof MetricsService) {
            ScheduledExecutorService metricsUpdateScheduler = Executors.newSingleThreadScheduledExecutor();
            metricsUpdateScheduler.scheduleAtFixedRate(this::triggerUpdateMetrics,
                    0, 1, java.util.concurrent.TimeUnit.SECONDS);
        }
    }

    private void triggerUpdateMetrics() {
        ((MetricsService) metricsService).updateMetrics();
    }

    private void buildMetrics(MeterRegistry registry) {
        Gauge.builder("wirerest_network_transmit_bytes_total", metrics, metrics -> metrics.transferTxTotal.get())
                .tag("type", "transfer")
                .description("Total transferred bytes").register(registry);

        Gauge.builder("wirerest_network_transmit_bytes_total", metrics, metrics -> metrics.transferRxTotal.get())
                .description("Total received bytes")
                .tag("type", "receive").register(registry);

        Gauge.builder("wirerest_peers_total", metrics, metrics -> metrics.totalPeers.get())
                .description("Total peers in wg interface").register(registry);

        Gauge.builder("wirerest_iface_ipv4_free", metrics, metrics -> metrics.freeV4Ips.get())
                .description("The number of IP addresses that new clients can use").register(registry);

        Gauge.builder("wirerest_iface_ipv4_total", metrics, metrics -> metrics.totalV4Ips.get())
                .description("Number of IPs in current wg interface").register(registry);
    }


}