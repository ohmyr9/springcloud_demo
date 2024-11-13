package com.ronnie.product.service;

import com.ronnie.product.api.dto.BatchUpdateInventoryReqDTO;
import com.ronnie.product.api.dto.DecreaseInventory;
import com.ronnie.product.model.Inventory;
import com.ronnie.product.model.InventoryChangeLog;
import com.ronnie.product.repo.InventoryChangeLogRepository;
import com.ronnie.product.repo.InventoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class InventoryServiceTest {
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private InventoryChangeLogRepository inventoryChangeLogRepository;
    @Autowired
    private InventoryRepository inventoryRepository;
    @Test
//    @Sql("classpath:test_data.sql")
    void decreaseInventory() {
        String requestId = UUID.randomUUID().toString();
        int productOne = 1;
        int productTwo = 2;
        int quantityOne = 10;
        int quantityTwo = 20;
        BatchUpdateInventoryReqDTO req = BatchUpdateInventoryReqDTO.builder()
                .requestId(requestId)
                .decreaseInventories(List.of(new DecreaseInventory(productOne, quantityOne),
                        new DecreaseInventory(productTwo, quantityTwo)))
                .build();


        Inventory inventoryOne = inventoryRepository.findByProductId(productOne).get();
        Inventory inventoryTwo = inventoryRepository.findByProductId(productTwo).get();

        inventoryService.decreaseInventory(req);

        Optional<InventoryChangeLog> logOptional = inventoryChangeLogRepository.findByRequestId(requestId);
        assertThat(logOptional).isPresent();
        InventoryChangeLog log = logOptional.get();
        assertThat(log.getItemLogs().size()).isEqualTo(2);
        assertThat(log.getItemLogs().stream().collect(Collectors.toMap(i -> i.getProductId(), i -> i.getQuantity())))
                .containsEntry(productOne,-quantityOne)
                .containsEntry(productTwo,-quantityTwo);
        Inventory updatedInventoryOne = inventoryRepository.findByProductId(productOne).get();
        Inventory updatedInventoryTwo = inventoryRepository.findByProductId(productTwo).get();
        assertThat(updatedInventoryOne.getQuantity()).isEqualTo(inventoryOne.getQuantity()-quantityOne);
        assertThat(updatedInventoryTwo.getQuantity()).isEqualTo(inventoryTwo.getQuantity()-quantityTwo);
    }
}