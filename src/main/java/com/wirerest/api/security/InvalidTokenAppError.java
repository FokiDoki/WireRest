package com.wirerest.api.security;

import com.wirerest.api.AppError;

public class InvalidTokenAppError extends AppError {
    public InvalidTokenAppError() {
        super(498, "Token is invalid or not provided");
    }
}
