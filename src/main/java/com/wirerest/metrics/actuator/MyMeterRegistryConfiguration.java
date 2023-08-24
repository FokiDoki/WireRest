package com.wirerest.metrics.actuator;

import com.wirerest.metrics.MetricsService;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class MyMeterRegistryConfiguration {

    MeterRegistry registry;
    MetricsService metricsService;
    @Autowired
    public MyMeterRegistryConfiguration(MeterRegistry registry) {
        this.registry = registry;
        registry.gauge("test", 10);

    }



}