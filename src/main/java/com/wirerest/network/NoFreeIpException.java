package com.wirerest.network;

public class NoFreeIpException extends RuntimeException {
    public NoFreeIpException(String message) {
        super(message);
    }
}
