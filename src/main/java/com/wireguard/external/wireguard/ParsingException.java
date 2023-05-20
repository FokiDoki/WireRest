package com.wireguard.external.wireguard;

public class BadInterfaceException extends Exception {
    public BadInterfaceException(String message, Exception e) {
        super(message, e);
    }
}
