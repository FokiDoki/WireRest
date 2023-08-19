package com.wirerest.api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.wirerest.api.AppError;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Controller
public class AuthEntryPoint implements ServerAuthenticationEntryPoint {

    private ObjectMapper jsonMapper = new JsonMapper();

    @SneakyThrows
    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        AppError error = new AppError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized");

        // Set the status code and response body
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory()
                .wrap(jsonMapper.writeValueAsBytes(error))));
    }
}