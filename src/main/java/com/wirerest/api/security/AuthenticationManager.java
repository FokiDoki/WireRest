package com.wirerest.api.security;

import com.wirerest.api.security.authentication.NoAuthentication;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationManager implements ReactiveAuthenticationManager {

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        authentication.setAuthenticated(!(authentication instanceof NoAuthentication));
        return Mono.just(authentication);
    }
}