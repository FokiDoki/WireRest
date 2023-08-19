package com.wirerest.api.security;

import com.wirerest.api.AppError;

public class UnauthorizedAppError extends AppError {
    public UnauthorizedAppError() {
        super(401, "Unauthorized");
    }
}
