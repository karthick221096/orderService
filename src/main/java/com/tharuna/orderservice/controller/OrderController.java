package com.tharuna.orderservice.controller;

import com.tharuna.orderservice.dtos.CreateOrderRequestDto;
import com.tharuna.orderservice.dtos.CreateOrderResponseDto;
import com.tharuna.orderservice.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<CreateOrderResponseDto> createOrder(@RequestBody CreateOrderRequestDto request) {
        CreateOrderResponseDto response = orderService.createOrder(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/confirm")
    public ResponseEntity<String> confirmOrder(@RequestParam("orderId") Long orderId) {
        orderService.confirmOrder(orderId);
        return ResponseEntity.ok("Order confirmed: " + orderId);
    }
}
