package com.ronnie.gateway.circuitbreaker;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.ServiceInstanceListSuppliers;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "httpbin=http://localhost:${wiremock.server.port}",
                "resilience4j.circuitbreaker.instances.order.sliding-window-size=1"
        }
)
@AutoConfigureWireMock(port = 0)
class GatewayApplicationTest {

    @Autowired
    protected WebTestClient webClient;

    @Test
    @WithMockUser
    public void circuitBreakerTest() {
        stubFor(get(urlEqualTo("/v1/orders/1"))
                .willReturn(aResponse()
                        .withBody("{\"headers\":{\"Hello\":\"World\"}}")
                        .withFixedDelay(3000)
                        .withHeader("Content-Type", "application/json")));
        stubFor(get(urlEqualTo("/v1/orders/2"))
                .willReturn(aResponse()
                        .withBody("{\"headers\":{\"Hello\":\"World\"}}")
                        .withHeader("Content-Type", "application/json")));
        webClient
                .get().uri("/v1/orders/2")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                        .jsonPath("$.headers.Hello").isEqualTo("World");
        //trigger circuit breaker
        webClient
                .get().uri("/v1/orders/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody().consumeWith(response -> {
                    assertThat(response.getResponseBody()).isEqualTo("fallback".getBytes());
                });
        webClient
                .get().uri("/v1/orders/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody().consumeWith(response -> {
                    assertThat(response.getResponseBody()).isEqualTo("fallback".getBytes());
                });
        //when the circuit breaker is open, the following response will be fallback
        webClient
                .get().uri("/v1/orders/2")
                .exchange()
                .expectStatus().isOk()
                .expectBody().consumeWith(response -> {
                    assertThat(response.getResponseBody()).isEqualTo("fallback".getBytes());
                });
    }

    @TestConfiguration
    static class LoadBalancerConfig {
        @Value("${wiremock.server.port}")
        private Integer port;

        @Bean
        public ServiceInstanceListSupplier fixedServiceInstanceListSupplier(Environment env) {
            return ServiceInstanceListSuppliers.from("backend",
                    new DefaultServiceInstance("backend-1", "backend", "localhost", port, false));
        }
    }
}
