package com.ronnie.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
@AutoConfigureWebTestClient(timeout = "36000")
public class HelloSecurityTests {

	@Autowired
	WebTestClient rest;

	@Test
	void indexWhenUnAuthenticatedThenRedirect() throws Exception {
		this.rest.get()
			.uri("/v1/orders/1")
			.exchange()
			.expectStatus().isUnauthorized();
	}

	@Test
	@WithMockUser
	void indexWhenAuthenticatedThenOk() throws Exception {
		this.rest.get()
			.uri("/v1/orders/1")
			.exchange()
			.expectStatus().isOk();
	}

}