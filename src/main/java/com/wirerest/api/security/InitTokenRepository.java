package com.wirerest.api.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class InitTokenRepository implements TokenRepository {

    private final Token INIT_TOKEN;


    public InitTokenRepository(@Value("${security.token}") String InitToken){
        INIT_TOKEN = new Token(InitToken);
    }

    @Override
    public void add(Token token) {
        throw new UnsupportedOperationException("Cannot add token to init repository");
    }

    @Override
    public void remove(Token token) {
        throw new UnsupportedOperationException("Cannot remove token from init repository");
    }

    @Override
    public void update(Token oldT, Token newT) {
        throw new UnsupportedOperationException("Cannot update token in init repository");
    }

    @Override
    public Optional<Token> getByValue(String value) {
        if (value.equals(INIT_TOKEN.getValue()))
            return Optional.of(INIT_TOKEN);
        else
            return Optional.empty();
    }

    @Override
    public List<Token> getAll() {
        return List.of(INIT_TOKEN);
    }
}
