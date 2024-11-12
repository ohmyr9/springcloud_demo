package com.ronnie.order.remote;

import com.ronnie.common.api.Result;
import com.ronnie.common.constant.ServiceNames;
import com.ronnie.common.exception.UnknownException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class ProductRemoteService {
    public static final String PRODUCT_SERVICE_NAME = "http://" + ServiceNames.PRODUCT_SERVICE;
    private final RestTemplate restTemplate;

    public ProductRemoteService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void decreaseInventory(BatchDecreaseInventoryReqDTO req) {
        RequestEntity<BatchDecreaseInventoryReqDTO> request = RequestEntity
                .post(PRODUCT_SERVICE_NAME + "/v1/inventories")
                .accept(MediaType.APPLICATION_JSON)
                .body(req);
        ResponseEntity<Result<Void>> responseEntity = restTemplate.exchange(request, new ParameterizedTypeReference<Result<Void>>() {
        });
        if(!responseEntity.getStatusCode().is2xxSuccessful()) {
            //handle error codes
            //log
        }
    }

    public Iterable<ProductDTO> findProductsByIds(Iterable<Integer> productIds) {
        String productIdsStr = StreamSupport.stream(productIds.spliterator(), false)
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        URI uri = UriComponentsBuilder
                .fromUriString(PRODUCT_SERVICE_NAME + "/v1/products")
                .queryParam("ids", productIdsStr).build().encode().toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<Result<List<ProductDTO>>> responseEntity =
                restTemplate.exchange(uri, HttpMethod.GET, requestEntity,
                        new ParameterizedTypeReference<>() {
                        });
        Result<List<ProductDTO>> body = responseEntity.getBody();
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            if (Result.isSuccess(body)) {
                return body.getData();
            } else {
                log.error("restAPI protocol error! response body doesn't correspond to api, body = {}", body);
                throw new UnknownException();
            }
        } else {
            if (body != null) {
                Integer errorCodes = body.getCode();
                //todo handle errors according to errorCodes
                throw new RuntimeException();
            } else {
                //unknown error
                throw new UnknownException();
            }
        }
    }

}
