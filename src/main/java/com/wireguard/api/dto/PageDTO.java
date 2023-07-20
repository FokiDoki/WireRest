package com.wireguard.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@AllArgsConstructor
public class PageDTO<T> {
    private final int totalPages;
    private final int currentPage;
    private final List<T> content;



}
