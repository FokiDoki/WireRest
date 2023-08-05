package com.wirerest.api.converters;

import com.wirerest.api.dto.PageDTO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;

public class PageDTOFromPageConverter<T> implements Converter<Page<T>, PageDTO<T>> {
    @Override
    public PageDTO<T> convert(Page<T> source) {
        return new PageDTO<T>(
                source.getTotalPages(),
                source.getNumber(),
                source.getContent()
        );
    }
}
