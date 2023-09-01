package com.wirerest.api.openAPI.examples;

import com.wirerest.api.openAPI.examples.samples.ExampleStatsSnapshotDTO;
import org.springframework.stereotype.Component;

@Component
public class StatsExample extends IdentifiedExample{
    public StatsExample() {
        super("stats");
        summary("Stats");
        setValue(new ExampleStatsSnapshotDTO());
    }
}
