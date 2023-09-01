package com.wirerest.api.openAPI.examples.samples.exceptions;

import com.wirerest.api.AppError;
import com.wirerest.api.GlobalExceptionHandler;
import com.wirerest.wireguard.PageOutOfRangeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SampleInvalidPage extends AppError {

    @Autowired
    public SampleInvalidPage(GlobalExceptionHandler exceptionHandler) {
        super();
        PageOutOfRangeException ex = new PageOutOfRangeException(101, 100);
        AppError alreadyExistsError = exceptionHandler.catchIllegalArgumentException(ex).getBody();
        setCode(alreadyExistsError.getCode());
        setMessage(alreadyExistsError.getMessage());
    }
}
