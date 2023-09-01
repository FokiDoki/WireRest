package com.wirerest.api.openAPI.examples.samples.exceptions;

import com.wirerest.api.AppError;
import com.wirerest.api.GlobalExceptionHandler;
import com.wirerest.api.openAPI.examples.samples.PublicKeyDTOSample;
import com.wirerest.wireguard.peer.PeerNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class SamplePeerNotFound extends AppError {
    public SamplePeerNotFound(GlobalExceptionHandler exceptionHandler) {
        super();
        PublicKeyDTOSample publicKey = new PublicKeyDTOSample();
        AppError notFoundError = exceptionHandler.catchResourceNotFoundException(
                new PeerNotFoundException(publicKey.getValue())
        ).getBody();
        setCode(notFoundError.getCode());
        setMessage(notFoundError.getMessage());
    }
}
