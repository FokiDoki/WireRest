package com.wirerest.metrics;

public interface IMetricsService {

    StatsSnapshot snapshot();

    WireRestMetrics getMetrics();
}
