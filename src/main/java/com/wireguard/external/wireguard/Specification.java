package com.wireguard.external.wireguard;

public interface Specification<T> {
    boolean isExist(T t);
}
