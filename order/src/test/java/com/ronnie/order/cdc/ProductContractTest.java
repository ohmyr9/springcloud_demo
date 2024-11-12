package com.ronnie.order.cdc;

import com.ronnie.order.remote.ProductDTO;
import com.ronnie.order.remote.ProductRemoteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureStubRunner(
        stubsMode = StubRunnerProperties.StubsMode.LOCAL,
        ids = "com.ronnie:product:+:8081"
)
public class ProductContractTest {
    @Autowired
    private ProductRemoteService productRemoteService;
    @Test
    public void getProductsShouldWork() {
        Iterable<ProductDTO> productsByIds = productRemoteService.findProductsByIds(List.of(1, 2));
        assertThat(productsByIds).isNotEmpty();
        productsByIds.forEach(p->{
            assertThat(p.getId()).isNotNull();
            assertThat(p.getName()).isNotEmpty();
            assertThat(p.getPrice()).isNotNull();
        });
    }
}
