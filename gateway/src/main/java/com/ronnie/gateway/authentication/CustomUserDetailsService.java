package com.ronnie.gateway.authentication;

import com.ronnie.common.api.Result;
import com.ronnie.common.constant.ServiceNames;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.ReactiveUserDetailsPasswordService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
@Slf4j
@Service
public class CustomUserDetailsService implements ReactiveUserDetailsService, ReactiveUserDetailsPasswordService {
    private final WebClient.Builder webClientBuilder;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public CustomUserDetailsService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public Mono<UserDetails> updatePassword(UserDetails user, String newPassword) {
        return null;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        URI uri = UriComponentsBuilder.fromUriString("http://" + ServiceNames.USER_SERVICE + "/v1/users/authenticate")
                .queryParam("username", username)
                .build().encode().toUri();
        return webClientBuilder.build().get().uri(uri)
                .accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(new ParameterizedTypeReference<Result<UserDetailsDTO>>() {
                }).mapNotNull(r -> {
                    if (Result.isSuccess(r)) {
                        UserDetailsDTO data = r.getData();
                        logger.info("get user : {}", data.getUsername());
                        return data;
                    } else {
                        //log
                        return null;
                    }
                });

    }
}
