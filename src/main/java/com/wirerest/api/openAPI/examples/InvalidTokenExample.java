package com.wirerest.api.openAPI.examples;

import com.wirerest.api.security.InvalidTokenAppError;
import org.springframework.stereotype.Component;

@Component
public class InvalidTokenExample extends IdentifiedExample implements DefaultExample{

    public InvalidTokenExample() {
        super("InvalidToken");
        summary("Authorization token is invalid or not provided");
        setValue(new InvalidTokenAppError());
    }

}
