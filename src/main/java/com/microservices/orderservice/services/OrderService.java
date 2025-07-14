package com.microservices.orderservice.services;

import com.microservices.orderservice.model.Order;

import java.util.List;

public interface OrderService {

    long createOrder(Order order);

    List<Order> getAllOrders();
}
