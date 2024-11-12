package com.ronnie.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.Arrays;

import static com.ronnie.common.constant.RequestHeaders.CURRENT_USER;

@Slf4j
@SpringBootApplication
@EnableDiscoveryClient
@RestController
@RequestMapping("/aa")
public class GatewayApplication {
//    @Value("${spring.config.import}")
//    private String value;
//    @Autowired
//    private Environment environment;
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }



//    @GetMapping
//    public Mono<String> hello() {
//        String activeProfiles = Arrays.toString(this.environment.getActiveProfiles());
//        return Mono.just("active profiles = "+activeProfiles+", prop = "+value);
//    }

//    @Bean
//    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
//        return factory -> factory.configureDefault(
//                id -> new Resilience4JConfigBuilder(id)
//                        .circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
//                        .timeLimiterConfig(
//                                TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(2))
//                                        .build()
//                        )
//                        .build()
//        );
//    }

    @Bean
    public GlobalFilter customGlobalFilter(Tracer tracer) {
        return new GlobalFilter() {
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                return exchange.getPrincipal()
                        .map(Principal::getName)
                        .defaultIfEmpty("Default User")
                        .map(userName -> {
                            //adds header to proxied request
                            tracer.createBaggage(CURRENT_USER, userName);
//                            exchange.getRequest().mutate().header(CURRENT_USER, userName).build();
                            return exchange;
                        })
                        .flatMap(chain::filter);
            }
        };
    }

//    @Bean
//    public GlobalFilter customGlobalPostFilter() {
//        return (exchange, chain) -> chain.filter(exchange)
//                .then(Mono.just(exchange))
//                .map(serverWebExchange -> {
//                    //adds header to response
//                    serverWebExchange.getResponse().getHeaders().set("CUSTOM-RESPONSE-HEADER",
//                            HttpStatus.OK.equals(serverWebExchange.getResponse().getStatusCode()) ? "It worked": "It did not work");
//                    return serverWebExchange;
//                })
//                .then();
//    }

}

