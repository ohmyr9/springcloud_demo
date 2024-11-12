package com.ronnie.product.service;

import com.ronnie.product.api.dto.BatchDecreaseInventoryReq;
import com.ronnie.product.api.dto.DecreaseInventory;
import com.ronnie.product.exception.NotEnoughInventoryException;
import com.ronnie.product.exception.ProductNotExistException;
import com.ronnie.product.model.Inventory;
import com.ronnie.product.model.InventoryChangeItemLog;
import com.ronnie.product.model.InventoryChangeLog;
import com.ronnie.product.repo.InventoryChangeLogRepository;
import com.ronnie.product.repo.InventoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.StreamSupport.stream;

@Service
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final InventoryChangeLogRepository changeLogRepository;
    public InventoryService(InventoryRepository inventoryRepository, InventoryChangeLogRepository inventoryChangeLogRepository) {
        this.inventoryRepository = inventoryRepository;
        this.changeLogRepository = inventoryChangeLogRepository;
    }

    @Transactional
    public void decreaseInventory(@Valid BatchDecreaseInventoryReq req) {
        Set<Integer> productIds = req.getDecreaseInventories().stream().map(DecreaseInventory::getProductId).collect(Collectors.toSet());

        Map<Integer, Inventory> inventories =
                stream(inventoryRepository.findAllByProductIdIn(productIds).spliterator(), false)
                        .collect(toMap(Inventory::getProductId, Function.identity()));
        req.getDecreaseInventories().forEach(decreaseItem -> {
            Inventory inventory = inventories.get(decreaseItem.getProductId());
            //check products exists
            if (inventory == null) {
                throw new ProductNotExistException();
            }
            int quantity = inventory.getQuantity() - decreaseItem.getQuantity();
            if (quantity < 0) {
                throw new NotEnoughInventoryException();
            }
            //persistent decreased inventory
            inventoryRepository.save(new Inventory(inventory.getId(), inventory.getProductId(), quantity, inventory.getVersion()));
        });

        //insert inventory change logs
        Set<InventoryChangeItemLog> itemLogs = req.getDecreaseInventories().stream()
                .map(decreaseInventory -> new InventoryChangeItemLog(null, decreaseInventory.getProductId(), -decreaseInventory.getQuantity()))
                .collect(Collectors.toSet());
        InventoryChangeLog changeLog = new InventoryChangeLog(null, req.getRequestId(), itemLogs);
        changeLogRepository.save(changeLog);
    }
}
