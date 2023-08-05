package com.wirerest.wireguard;

import java.util.List;

public interface Repository<T> {
    void add(T t);

    void remove(T t);

    void update(T oldT, T newT);

    List<T> getBySpecification(Specification<T> specification);

    List<T> getByAllSpecifications(List<Specification<T>> specifications);

    List<T> getAll();

}
