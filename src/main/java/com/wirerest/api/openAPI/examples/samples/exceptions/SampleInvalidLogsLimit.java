package com.wirerest.api.openAPI.examples.samples.exceptions;

import com.wirerest.api.AppError;

public class SampleInvalidLogsLimit extends AppError {
    public SampleInvalidLogsLimit() {
        super(400,
                "getLogs.limit: must be greater than or equal to 0");
    }
}
