package com.wirerest.api.security;

import com.wirerest.api.security.authentication.AdminAuthentication;
import com.wirerest.api.security.authentication.NoAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class SecurityContextRepository implements ServerSecurityContextRepository {

    TokenRepository tokenRepository;

    @Autowired
    public SecurityContextRepository(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    private static final Logger logger = LoggerFactory.getLogger(SecurityContextRepository.class);

    @Override
    public Mono<Void> save(ServerWebExchange serverWebExchange, SecurityContext securityContext) {
        return null;
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange serverWebExchange) {
        String userRequestToken = getTokenFromRequest(serverWebExchange);
        String userBearerToken = getTokenFromAuthorizationHeader(serverWebExchange);
        String requestId = serverWebExchange.getRequest().getId();
        Authentication authentication;
        logger.debug("[%s] Checking request token '%s' and bearer '%s'".formatted(
                requestId,
                userRequestToken, userBearerToken));
        if (tokenRepository.getByValue(userRequestToken).isEmpty() && tokenRepository.getByValue(userBearerToken).isEmpty()) {
            authentication = new NoAuthentication();
        } else {
            authentication = new AdminAuthentication();
        }
        logger.debug("[%s] Authorized as %s".formatted(requestId, authentication.getClass().getSimpleName()));
        return Mono.just(new SecurityContextImpl(authentication));
    }

    private String getTokenFromRequest(ServerWebExchange exchange) {
        Object tokenObj = exchange.getRequest().getQueryParams().get("token");
        if (tokenObj == null) {
            return "";
        }
        String token = String.valueOf(tokenObj);
        token = token.substring(1, token.length() - 1);
        return token;
    }

    private String getTokenFromAuthorizationHeader(ServerWebExchange exchange) {
        String token = String.valueOf(exchange.getRequest().getHeaders().get("Authorization"));
        if (token == null || token.length() < 9) {
            return "";
        }
        token = token.substring(8, token.length() - 1);
        return token;
    }
}