package com.wirerest.api.security;

import com.wirerest.api.security.authentication.AdminAuthentication;
import com.wirerest.api.security.authentication.NoAuthentication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class SecurityContextRepository implements ServerSecurityContextRepository {

    @Value("${security.token}")
    String INITIAL_TOKEN;

    @Override
    public Mono<Void> save(ServerWebExchange serverWebExchange, SecurityContext securityContext) {
        // Don't know yet where this is for.
        return null;
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange serverWebExchange) {
        String token = String.valueOf(serverWebExchange.getRequest().getQueryParams().get("token"));

        Authentication authentication = (INITIAL_TOKEN.equals(token))?
                new AdminAuthentication(): new NoAuthentication();
        return Mono.just(new SecurityContextImpl(authentication));
    }
}