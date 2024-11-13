package com.ronnie.common.api;

import com.ronnie.common.constant.ServiceNames;
import com.ronnie.common.exception.UnknownException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Slf4j
public class UserRemoteService {
    public static final String USER_SERVICE_NAME = "http://" + ServiceNames.USER_SERVICE;

    private final RestTemplate restTemplate;

    public UserRemoteService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public UserDTO getUserByName(String userName) {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(USER_SERVICE_NAME + "/v1/users/name/" + userName)
                .build().encode().toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<Result<UserDTO>> responseEntity =
                restTemplate.exchange(uri, HttpMethod.GET, requestEntity,
                        new ParameterizedTypeReference<>() {
                        });
        Result<UserDTO> body = responseEntity.getBody();
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            if (Result.isSuccess(body)) {
                return body.getData();
            } else {
                log.error("restAPI protocol error! response body doesn't correspond to api, body = {}", body);
                throw new UnknownException();
            }
        } else {
            //todo handle errors
            throw new UnknownException();
        }
    }
}
