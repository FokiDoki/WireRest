package com.wirerest.api.security;

import com.wirerest.api.AppError;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class AccessDeniedHandler implements ServerAccessDeniedHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException ex) {
        AppError error = new InvalidTokenAppError();
        return Util.setErrorToResponse(exchange, error);
    }
}
