package com.wirerest.api.service;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class StatsSnapshotDto {
    private final long timestamp;
    private final long peers;
    private final long totalV4Ips;
    private final long freeV4Ips;
    private final long transferTx;
    private final long transferRx;
}
