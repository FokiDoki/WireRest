package com.wirerest.api.openAPI.examples.samples.exceptions;

import com.wirerest.api.AppError;
import com.wirerest.api.GlobalExceptionHandler;
import com.wirerest.api.openAPI.examples.samples.ExamplePublicKeyDTO;
import com.wirerest.wireguard.peer.PeerAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SamplePeerAlreadyExists extends AppError {

    @Autowired
    public SamplePeerAlreadyExists(GlobalExceptionHandler exceptionHandler) {
        super();
        ExamplePublicKeyDTO publicKey = new ExamplePublicKeyDTO();
        AppError alreadyExistsError = exceptionHandler.peerAlreadyExistsException(
                new PeerAlreadyExistsException(publicKey.getValue())
        ).getBody();
        setCode(alreadyExistsError.getCode());
        setMessage(alreadyExistsError.getMessage());
    }
}
