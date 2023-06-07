package com.wireguard.external.wireguard;

public class ParsingException extends RuntimeException {
    public ParsingException(String message, Exception e) {
        super(message, e);
    }
}
