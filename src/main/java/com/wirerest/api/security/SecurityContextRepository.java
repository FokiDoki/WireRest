package com.wirerest.api.security;

import com.wirerest.api.security.authentication.AdminAuthentication;
import com.wirerest.api.security.authentication.NoAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(SecurityContextRepository.class);

    @Override
    public Mono<Void> save(ServerWebExchange serverWebExchange, SecurityContext securityContext) {
        return null;
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange serverWebExchange) {
        String userToken = getTokenFromRequest(serverWebExchange);
        logger.debug("Checking token %s".formatted(userToken));
        Authentication authentication = (INITIAL_TOKEN.equals(userToken))?
                new AdminAuthentication(): new NoAuthentication();
        logger.debug("Authorized as %s".formatted(authentication.getClass().getSimpleName()));
        return Mono.just(new SecurityContextImpl(authentication));
    }

    private String getTokenFromRequest(ServerWebExchange exchange){
        String userToken = String.valueOf(exchange.getRequest().getQueryParams().get("token"));
        userToken = userToken.substring(1, userToken.length()-1);
        return userToken;
    }
}