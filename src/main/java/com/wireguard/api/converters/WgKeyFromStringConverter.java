package com.wireguard.api.converters;

import com.wireguard.api.dto.WgKey;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

@ReadingConverter
@Component
public class WgKeyFromStringConverter implements Converter<String, WgKey> {
    @Override
    public WgKey convert(String source) {
        return new WgKey(source);
    }
}
