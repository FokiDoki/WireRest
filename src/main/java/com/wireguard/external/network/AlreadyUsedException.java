package com.wireguard.external.network;

public class AlreadyUsedException extends RuntimeException {
    public AlreadyUsedException(String message) {
        super(message);
    }
}
