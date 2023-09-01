package com.wirerest.network;

public abstract class NoFreeIpException extends RuntimeException {
    public NoFreeIpException(String message) {
        super(message);
    }
}
