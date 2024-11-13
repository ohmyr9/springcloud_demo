package com.ronnie.gateway.security;

import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.security.Principal;

import static com.ronnie.common.constant.RequestHeaders.CURRENT_USER;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    private final Tracer tracer;

    public SecurityConfig(Tracer tracer) {
        this.tracer = tracer;
    }

    @Bean
    SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http) {
        http
                .addFilterAfter(
                        (exchange, chain) -> exchange
                                .getPrincipal()
                                .map(Principal::getName)
                                .defaultIfEmpty("")
                                .map(name -> {
                                    tracer.createBaggage(CURRENT_USER, name);
                                    return exchange;
                                }).then(chain.filter(exchange)), SecurityWebFiltersOrder.AUTHORIZATION)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.GET, "/v1/users").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.POST, "/v1/users").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.GET, "/v1/users/authenticate").denyAll()
                        .anyExchange().authenticated()
                )
                .httpBasic(withDefaults())
                .formLogin(withDefaults());

        return http.build();
    }
}
