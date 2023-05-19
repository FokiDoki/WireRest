package com.wireguard.parser;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;

public class NotABase64Exception extends UncheckedIOException {
    public NotABase64Exception(String string) {
        super(new UnsupportedEncodingException(
                "%s is not a valid base64 string".formatted(
                        string
                )
        ));
    }
}
