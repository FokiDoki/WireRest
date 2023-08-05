package com.wirerest.wireguard;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RepositoryPageable<T> extends Repository<T> {
    Page<T> getAll(Pageable pageable);
}
