package com.wireguard.external.shell;

import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class StreamToStringConverter implements Converter<InputStream, String> {

    private static final Logger logger = LoggerFactory.getLogger(StreamToStringConverter.class);
    private Charset charset = StandardCharsets.UTF_8;

    public StreamToStringConverter(Charset charset) {
        this.charset = charset;
    }

    public StreamToStringConverter() {

    }

    @Override
    @Nullable
    public String convert(InputStream source) {
        try {
            return new String(source.readAllBytes(), charset);
        } catch (IOException e) {
            logger.error("Error converting stream", e);
            return null;
        }
    }
}
