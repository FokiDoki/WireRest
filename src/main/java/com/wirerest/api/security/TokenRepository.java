package com.wirerest.api.security;

import java.util.List;
import java.util.Optional;

public interface TokenRepository {
    void add(Token t);

    void remove(Token t);

    void update(Token oldT, Token newT);

    Optional<Token> getByValue(String value);

    List<Token> getAll();

}
