package com.wirerest.api.converters;

import com.wirerest.api.dto.PageRequestDTO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class PageRequestFromDTOConverter implements Converter<PageRequestDTO, PageRequest> {
    @Override
    public PageRequest convert(PageRequestDTO source) {
        if (source.getLimit() == 0)
            return PageRequest.of(source.getPage(), Integer.MAX_VALUE, getSort(source.getSort()));
        return PageRequest.of(source.getPage(), source.getLimit(), getSort(source.getSort()));
    }

    private Sort getSort(String sortKey) {
        if (sortKey == null) {
            return Sort.unsorted();
        }
        String[] keys = sortKey.split("\\.");
        if (keys.length == 1) {
            return Sort.by(sortKey).descending();
        } else {
            Sort.Direction direction = Sort.Direction.fromString(keys[1]);
            return Sort.by(direction, keys[0]);
        }
    }

}
