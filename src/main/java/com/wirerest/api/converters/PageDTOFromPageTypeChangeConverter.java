package com.wirerest.api.converters;

import com.wirerest.api.dto.PageDTO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;

public class PageDTOFromPageTypeChangeConverter<T, U> implements Converter<Page<T>, PageDTO<U>> {
    private final Converter<T, U> converter;

    public PageDTOFromPageTypeChangeConverter(Converter<T, U> converter) {
        this.converter = converter;
    }

    @Override
    public PageDTO<U> convert(Page<T> source) {
        return new PageDTO<U>(
                source.getTotalPages(),
                source.getNumber(),
                source.map(converter::convert).getContent()
        );
    }

}
