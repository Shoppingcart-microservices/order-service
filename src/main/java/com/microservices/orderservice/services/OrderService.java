package com.microservices.orderservice.services;

import com.microservices.orderservice.model.Order;

public interface OrderService {

    long createOrder(Order order);
}
