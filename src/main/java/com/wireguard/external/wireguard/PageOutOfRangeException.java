package com.wireguard.external.wireguard;

public class PageOutOfRangeException extends IllegalArgumentException {
    public PageOutOfRangeException(int requestedPage, int totalPages) {
        super("Requested %d page, but total pages is %d".formatted(requestedPage, totalPages));
    }
}
