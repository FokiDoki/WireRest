package com.wirerest.api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.wirerest.api.AppError;
import lombok.SneakyThrows;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class Util {
    @SneakyThrows
    public static Mono<Void> setErrorToResponse(ServerWebExchange exchange, AppError error){
        ObjectMapper jsonMapper = new JsonMapper();
        exchange.getResponse().setRawStatusCode(error.getCode());
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory()
                .wrap(jsonMapper.writeValueAsBytes(error))));
    }
}
