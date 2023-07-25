package com.wireguard.external.wireguard;

public class PageOutOfRangeException extends IllegalArgumentException {
    public PageOutOfRangeException(int requestedPage, int totalPages) {
        super("Page %d requested, but total pages %d".formatted(requestedPage, totalPages));
    }
}
