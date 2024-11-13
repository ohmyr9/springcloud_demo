package com.ronnie.product.controller;

import com.ronnie.common.api.Result;
import com.ronnie.product.api.dto.BatchUpdateInventoryReqDTO;
import com.ronnie.product.api.dto.ChangeInventoryReqDTO;
import com.ronnie.product.api.dto.InventoryDTO;
import com.ronnie.product.api.dto.InventoryDetailDTO;
import com.ronnie.product.service.InventoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/v1/inventories")
@Slf4j
public class InventoryController {
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    Result<Void> batchUpdateInventory(@RequestBody @Valid BatchUpdateInventoryReqDTO batchUpdateInventoryReq) {
        log.info("decrease inventory: {}", batchUpdateInventoryReq);
        inventoryService.decreaseInventory(batchUpdateInventoryReq);
        return Result.success();
    }
    //todo provide reverse batch operation


    @PutMapping("/{productId}")
    Result<InventoryDTO> changeInventory(@RequestBody ChangeInventoryReqDTO reqDTO, @PathVariable("productId") @NotNull Integer productId) {
        reqDTO.setProductId(productId);
        return Result.success(inventoryService.changeInventory(reqDTO));
    }

    @GetMapping()
    Result<List<InventoryDetailDTO>> listInventory() {
        return Result.success(inventoryService.listAll());
    }
}
