package com.wireguard.api.dto;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class PageDTO<T> {
    private final int totalPages;
    private final int currentPage;
    private final int pageSize;
    private final List<T> data;

    public PageDTO(int totalPages, int currentPage, int pageSize, List<T> data) {
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.data = data;
    }

    public static <T> PageDTO<T> from(Page<T> page) {
        return new PageDTO<>(
                page.getTotalPages(),
                page.getNumber(),
                page.getSize(),
                page.getContent()
        );
    }
}
