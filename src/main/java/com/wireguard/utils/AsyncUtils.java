package com.wireguard.utils;

import lombok.SneakyThrows;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AsyncUtils {

    @SneakyThrows
    public static <T> T await(Future<T> future){
        try {
            return future.get();
        } catch (ExecutionException e) {
            throw e.getCause();
        }
    }
}
