package com.wireguard.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PageRequestDTO {
    @NotNull
    @Min(0)
    private int page = 0;
    @NotNull
    @Min(0)
    private int limit = 0;
    private String sort;


}
