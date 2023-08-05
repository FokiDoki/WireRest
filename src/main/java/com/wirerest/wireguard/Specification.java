package com.wirerest.wireguard;

public interface Specification<T> {
    boolean isExist(T t);
}
