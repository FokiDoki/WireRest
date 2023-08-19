package com.wirerest.api.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.security.web.server.util.matcher.AndServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.OrServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final String INITIAL_TOKEN;


    public SecurityConfig(
            @Value("${security.token}") String initialToken
    ){
        this.INITIAL_TOKEN = initialToken;
    }

    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        UserDetails user = User
                .withUsername("root")
                .password(INITIAL_TOKEN)
                .roles(Role.FULL_ACCESS)
                .build();
        return new MapReactiveUserDetailsService(user);
    }

    @Order(Ordered.LOWEST_PRECEDENCE)
    @Bean
    SecurityWebFilterChain apiHttpSecurity(ServerHttpSecurity http) {
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