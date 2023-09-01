package com.wirerest.api.openAPI.examples.samples.exceptions;

import com.wirerest.api.AppError;
import com.wirerest.api.GlobalExceptionHandler;
import com.wirerest.network.AlreadyUsedException;
import com.wirerest.network.Subnet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SampleAlreadyUsed extends AppError {

    @Autowired
    public SampleAlreadyUsed(GlobalExceptionHandler exceptionHandler) {
        super();
        Subnet subnet = Subnet.valueOf("10.0.0.12/32");
        AppError alreadyExistsError = exceptionHandler.alreadyUsed(
                new AlreadyUsedException(subnet)
        ).getBody();
        setCode(alreadyExistsError.getCode());
        setMessage(alreadyExistsError.getMessage());
    }
}
