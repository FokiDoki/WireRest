package com.wirerest.api.openAPI.examples.samples.exceptions;

import com.wirerest.api.AppError;

public class SampleInvalidPubKey extends AppError {
    public SampleInvalidPubKey() {
        super(400,
                "publicKey.value: Invalid key format (Base64 required) (test provided), " +
                        "publicKey.value: Key must be 44 characters long (test provided)");
    }
}
