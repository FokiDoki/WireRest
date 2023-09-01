package com.wirerest.api.openAPI.examples;

import com.wirerest.api.openAPI.examples.samples.exceptions.SampleNoFreeIpInRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RangeNoFreeIpExample extends IdentifiedExample{

    @Autowired
    public RangeNoFreeIpExample(SampleNoFreeIpInRange noFreeIpInRange) {
        super("RangeNoFreeIp500");
        summary("No free ip");
        setValue(noFreeIpInRange);
    }
}
