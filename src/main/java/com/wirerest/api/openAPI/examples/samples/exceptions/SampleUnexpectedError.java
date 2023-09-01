package com.wirerest.api.openAPI.examples.samples.exceptions;

import com.wirerest.api.AppError;

public class SampleUnexpectedError extends AppError {
    public SampleUnexpectedError() {
        super(500,
                "Unexpected error");
    }
}
