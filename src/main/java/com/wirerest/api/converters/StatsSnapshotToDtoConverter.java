package com.wirerest.api.converters;

import com.wirerest.api.service.StatsSnapshotDto;
import com.wirerest.stats.StatsSnapshot;
import org.springframework.core.convert.converter.Converter;

public class StatsSnapshotToDtoConverter implements Converter<StatsSnapshot, StatsSnapshotDto> {
    @Override
    public StatsSnapshotDto convert(StatsSnapshot source) {
        return new StatsSnapshotDto(
                source.getTimestamp().toEpochMilli(),
                source.getTotalPeers(),
                source.getTotalV4Ips(),
                source.getFreeV4Ips(),
                source.getTransferTxTotal(),
                source.getTransferRxTotal()
        );
    }
}
