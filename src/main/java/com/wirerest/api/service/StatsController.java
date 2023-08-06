package com.wirerest.api.service;

import com.wirerest.api.converters.StatsSnapshotToDtoConverter;
import com.wirerest.logs.LogsDao;
import com.wirerest.stats.StatsService;
import com.wirerest.stats.StatsSnapshot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class StatsController {

    StatsService statsService;
    StatsSnapshotToDtoConverter statsSnapshotToDtoConverter = new StatsSnapshotToDtoConverter();
    @Autowired
    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/v1/service/stats")
    public StatsSnapshotDto getStats() {
        return statsSnapshotToDtoConverter.convert(statsService.snapshot());
    }

}
