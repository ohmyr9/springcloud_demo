package com.ronnie.product.service;

import com.ronnie.product.api.dto.*;
import com.ronnie.product.exception.NotEnoughInventoryException;
import com.ronnie.product.exception.ProductNotExistException;
import com.ronnie.product.model.Inventory;
import com.ronnie.product.model.InventoryChangeItemLog;
import com.ronnie.product.model.InventoryChangeLog;
import com.ronnie.product.model.Product;
import com.ronnie.product.repo.InventoryChangeLogRepository;
import com.ronnie.product.repo.InventoryRepository;
import com.ronnie.product.repo.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.StreamSupport.stream;

@Service
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final InventoryChangeLogRepository changeLogRepository;
    private final ProductRepository productRepository;

    public InventoryService(InventoryRepository inventoryRepository, InventoryChangeLogRepository inventoryChangeLogRepository, ProductRepository productRepository) {
        this.inventoryRepository = inventoryRepository;
        this.changeLogRepository = inventoryChangeLogRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public void decreaseInventory(@Valid BatchUpdateInventoryReqDTO req) {
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

    public InventoryDTO changeInventory(ChangeInventoryReqDTO reqDTO) {
        return inventoryRepository.findByProductId(reqDTO.getProductId())
                .map(inventory -> {
                    int quantity = reqDTO.getQuantity() - inventory.getQuantity();
                    if (quantity == 0) {
                        return new InventoryDTO(reqDTO.getProductId(), reqDTO.getQuantity());
                    }
                    inventoryRepository.save(new Inventory(inventory.getId(), inventory.getProductId(), quantity, inventory.getVersion()));
                    Set<InventoryChangeItemLog> itemLogs = Set.of(new InventoryChangeItemLog(null, reqDTO.getProductId(), quantity));
                    InventoryChangeLog changeLog = new InventoryChangeLog(null, UUID.randomUUID().toString(), itemLogs);
                    changeLogRepository.save(changeLog);
                    return new InventoryDTO(reqDTO.getProductId(), reqDTO.getQuantity());
                }).orElseThrow(ProductNotExistException::new);

    }

    public void deleteInventory(Integer id) {
        inventoryRepository.deleteByProductId(id);
    }

    public List<InventoryDetailDTO> listAll() {
        Iterable<Inventory> inventories = inventoryRepository.findAll();
        List<Integer> productIds = stream(inventories.spliterator(), false).map(Inventory::getProductId).collect(Collectors.toList());
        Map<Integer, Product> productMap = stream(productRepository.findAllById(productIds).spliterator(), false).collect(toMap(p -> p.getId(), Function.identity()));
        return stream(inventories.spliterator(), false).map(inventory -> {
            Product product = productMap.get(inventory.getProductId());
            if (product == null) {
                throw new ProductNotExistException();
            }
            return InventoryDetailDTO.builder()
                    .productId(inventory.getProductId())
                    .quantity(inventory.getQuantity())
                    .price(product.getPrice())
                    .name(product.getName())
                    .build();
        }).collect(Collectors.toList());
    }
}
