package com.ronnie.product.controller;

import com.ronnie.common.api.Result;
import com.ronnie.product.api.dto.BatchDecreaseInventoryReq;
import com.ronnie.product.service.InventoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/inventories")
@Slf4j
public class InventoryController {
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    Result<Void> decreaseInventory(@RequestBody @Valid BatchDecreaseInventoryReq decreaseInventoryReq) {
        log.info("decrease inventory: {}", decreaseInventoryReq);
        inventoryService.decreaseInventory(decreaseInventoryReq);
        return Result.success();
    }
}
