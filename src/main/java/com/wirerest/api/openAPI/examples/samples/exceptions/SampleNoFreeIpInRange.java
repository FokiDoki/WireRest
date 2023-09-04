package com.wirerest.api.openAPI.examples.samples.exceptions;

import com.wirerest.api.AppError;
import com.wirerest.api.GlobalExceptionHandler;
import com.wirerest.network.NoFreeIpInRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SampleNoFreeIpInRange extends AppError {

    @Autowired
    public SampleNoFreeIpInRange(GlobalExceptionHandler exceptionHandler) {
        super();
        NoFreeIpInRange noFreeIpInRange = new NoFreeIpInRange("10.0.0.0", "10.0.0.255");
        AppError notFoundError = new AppError(500, noFreeIpInRange.getMessage());
        setCode(notFoundError.getCode());
        setMessage(notFoundError.getMessage());
    }
}
