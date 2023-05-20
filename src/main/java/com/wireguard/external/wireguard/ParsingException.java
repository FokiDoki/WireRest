package com.wireguard.external.wireguard;

public class ParsingException extends Exception {
    public ParsingException(String message, Exception e) {
        super(message, e);
    }
}
