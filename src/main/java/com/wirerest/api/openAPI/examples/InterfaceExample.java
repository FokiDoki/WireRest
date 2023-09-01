package com.wirerest.api.openAPI.examples;

import com.wirerest.api.openAPI.examples.samples.InterfaceDTOSample;
import org.springframework.stereotype.Component;

@Component
public class InterfaceExample extends IdentifiedExample{
    public InterfaceExample() {
        super("interface");
        summary("Interface");
        setValue(new InterfaceDTOSample());
    }
}
