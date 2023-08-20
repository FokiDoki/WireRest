package com.wirerest.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.OrServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private SecurityContextRepository securityContextRepository;




    @Order(Ordered.LOWEST_PRECEDENCE)
    @Bean
    SecurityWebFilterChain apiHttpSecurity(ServerHttpSecurity http) {
        http.authenticationManager(this.authenticationManager);
        http.securityContextRepository(this.securityContextRepository);
        http
                .authorizeExchange((exchanges) -> exchanges
                        .anyExchange().authenticated()
                ).exceptionHandling()
                    .accessDeniedHandler(new AccessDeniedHandler())
                     .authenticationEntryPoint(new AuthEntryPoint());
        return http.build();
    }

    @Order(30)
    @Bean
    SecurityWebFilterChain swaggerSecurity(ServerHttpSecurity http) {
        http
                .securityMatcher(new OrServerWebExchangeMatcher(
                        List.of(new PathPatternParserServerWebExchangeMatcher("/webjars/swagger-ui/**"),
                                new PathPatternParserServerWebExchangeMatcher("/swagger-ui"),
                                new PathPatternParserServerWebExchangeMatcher("/v3/api-docs/swagger-config"),
                                new PathPatternParserServerWebExchangeMatcher("/v3/api-docs")
                        )
                    )
                ).authorizeExchange((exchanges) -> exchanges.anyExchange().permitAll());
        return http.build();
    }

}