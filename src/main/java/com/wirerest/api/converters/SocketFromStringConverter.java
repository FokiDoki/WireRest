package com.wirerest.api.converters;

import com.wirerest.api.dto.Socket;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

@ReadingConverter
@Component
public class SocketFromStringConverter implements Converter<String, Socket> {
    @Override
    public Socket convert(String source) {
        if (source.isEmpty()) return null;
        return new Socket(source.split(":")[0], Integer.parseInt(source.split(":")[1]));
    }
}
