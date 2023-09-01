package com.wirerest.api.openAPI.examples.samples.exceptions;

import com.wirerest.api.AppError;
import com.wirerest.api.GlobalExceptionHandler;
import com.wirerest.api.openAPI.examples.samples.ExamplePublicKeyDTO;
import com.wirerest.wireguard.peer.PeerNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class SamplePeerNotFound extends AppError {
    public SamplePeerNotFound(GlobalExceptionHandler exceptionHandler) {
        super();
        ExamplePublicKeyDTO publicKey = new ExamplePublicKeyDTO();
        AppError notFoundError = exceptionHandler.catchResourceNotFoundException(
                new PeerNotFoundException(publicKey.getValue())
        ).getBody();
        setCode(notFoundError.getCode());
        setMessage(notFoundError.getMessage());
    }
}
