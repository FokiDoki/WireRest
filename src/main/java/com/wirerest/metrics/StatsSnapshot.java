package com.wirerest.metrics;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Getter
public class StatsSnapshot {
    private final Instant timestamp;
    private final long totalPeers;
    private final long totalV4Ips;
    private final long freeV4Ips;
    private final long transferTxTotal;
    private final long transferRxTotal;

    public static class Builder {
        private Instant timestamp;
        private long totalPeers;
        private long totalV4Ips;
        private long freeV4Ips;
        private long transferTxTotal;
        private long transferRxTotal;

        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder totalPeers(long totalPeers) {
            this.totalPeers = totalPeers;
            return this;
        }

        public Builder totalV4Ips(long totalV4Ips) {
            this.totalV4Ips = totalV4Ips;
            return this;
        }

        public Builder freeV4Ips(long freeV4Ips) {
            this.freeV4Ips = freeV4Ips;
            return this;
        }

        public Builder transferTxTotal(long transferTxTotal) {
            this.transferTxTotal = transferTxTotal;
            return this;
        }

        public Builder transferRxTotal(long transferRxTotal) {
            this.transferRxTotal = transferRxTotal;
            return this;
        }

        public StatsSnapshot build() {
            return new StatsSnapshot(timestamp, totalPeers, totalV4Ips, freeV4Ips, transferTxTotal, transferRxTotal);
        }

    }
}
