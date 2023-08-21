package com.wirerest.api.security;

import lombok.SneakyThrows;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class Util {
    @SneakyThrows
    public static Mono<Void> setErrorToResponse(ServerWebExchange exchange, int httpErrorCode, byte[] error){
        exchange.getResponse().setRawStatusCode(httpErrorCode);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory()
                .wrap(error)));
    }
}
