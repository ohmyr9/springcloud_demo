package com.ronnie.order.controller;


import com.ronnie.common.api.Result;
import com.ronnie.common.api.UserDTO;
import com.ronnie.common.api.UserRemoteService;
import com.ronnie.common.constant.RequestHeaders;
import com.ronnie.order.api.dto.OrderDetailResDTO;
import com.ronnie.order.api.dto.PlaceOrderReqDTO;
import com.ronnie.order.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/v1/orders")
public class OrderController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final OrderService orderService;
    private final UserRemoteService userRemoteService;

    public OrderController(OrderService orderService, UserRemoteService userRemoteService) {
        this.orderService = orderService;
        this.userRemoteService = userRemoteService;
    }

    @PostMapping
    public Result<OrderDetailResDTO> placeOrder(@RequestBody @Valid PlaceOrderReqDTO placeOrderReq) {
        logger.info("placeOrder: " + placeOrderReq);
        return Result.success(orderService.placeOrder(placeOrderReq));
    }

    @GetMapping
    public Result<List<OrderDetailResDTO>> listOrders(@RequestHeader(RequestHeaders.CURRENT_USER) @NotNull String currentUser) {
        logger.info("listOrders: " + currentUser);
        UserDTO userDTO = userRemoteService.getUserByName(currentUser);
//        return Result.success(orderService.listOrders());
        return Result.success(orderService.listOrders(userDTO.getId()));
    }

    @GetMapping("/{id}")
    public Result<OrderDetailResDTO> orderDetail(@PathVariable("id") @NotNull Integer id, @RequestHeader(RequestHeaders.CURRENT_USER) String currentUser) {
        logger.info("order detail: " + id);
        return Result.success(orderService.orderDetail(id));
    }

}
