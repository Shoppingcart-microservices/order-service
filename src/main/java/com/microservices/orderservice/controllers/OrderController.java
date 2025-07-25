package com.microservices.orderservice.controllers;

import com.microservices.orderservice.model.Order;
import com.microservices.orderservice.model.OrderResponse;
import com.microservices.orderservice.services.OrderService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/order")
@Log4j2
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<Order>> getOrders() {
        log.info("=> Getting Orders");
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @PreAuthorize("hasAuthority('Admin') || hasAuthority('Customer')")
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable("id") long orderId) {
        log.info("=> Getting Order by ID {}", orderId);
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @PreAuthorize("hasAuthority('Customer')")
    @PostMapping
    public ResponseEntity<Long> createOrder(@RequestBody Order order) {
        long orderId = orderService.createOrder(order);
        log.info("Creating order with id: {}", orderId);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderId);
    }
}
