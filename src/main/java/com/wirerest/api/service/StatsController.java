package com.wirerest.api.service;

import com.wirerest.api.converters.StatsSnapshotToDtoConverter;
import com.wirerest.metrics.IMetricsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class StatsController {

    IMetricsService metricsService;
    StatsSnapshotToDtoConverter statsSnapshotToDtoConverter = new StatsSnapshotToDtoConverter();

    @Autowired
    public StatsController(IMetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @Operation(summary = "Get application statistics",
            description = """
                    Timestamp - Unix milliseconds timestamp of the stats snapshot
                                        
                    peers - number of peers in the interface
                                        
                    totalV4Ips - number of IPv4 addresses (/32) in the interface
                                        
                    freeV4Ips - number of addresses (/32) that are not in use
                                        
                    transferTx - total transmitted bytes for all peers
                                        
                    transferRx - total received bytes for all peers""",
            tags = {"Service"},
            security = @SecurityRequirement(name = "Token"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = {@Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = StatsSnapshotDto.class)),
                                    examples = {
                                            @ExampleObject(name = "Stats",
                                                    ref = "#/components/examples/stats")
                                    })})
            })
    @GetMapping("/v1/service/stats")
    public StatsSnapshotDto getStats() {
        return statsSnapshotToDtoConverter.convert(metricsService.snapshot());
    }


}
