package com.wirerest.api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.wirerest.api.AppError;
import lombok.SneakyThrows;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class AccessDeniedHandler implements ServerAccessDeniedHandler {

    private final AppError error = new InvalidTokenAppError();
    private final byte[] errorJson;
    @SneakyThrows
    public AccessDeniedHandler() {
        ObjectMapper jsonMapper = new JsonMapper();
        errorJson = jsonMapper.writeValueAsBytes(error);
        System.out.println("Created Json Error ");
    }
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException ex) {

        return Util.setErrorToResponse(exchange, error.getCode(), errorJson);
    }
}
