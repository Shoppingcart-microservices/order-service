package com.microservices.orderservice.services;

import com.microservices.orderservice.model.Order;

public interface OrderService {

    Long createOrder(Order order);
}
