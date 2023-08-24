package com.wirerest.metrics;

public class WireRestMetrics {
    public final LongMetric totalPeers = new LongMetric();
    public final LongMetric totalV4Ips = new LongMetric();
    public final LongMetric freeV4Ips = new LongMetric();
    public final LongMetric transferTxTotal = new LongMetric();
    public final LongMetric transferRxTotal = new LongMetric();
}
