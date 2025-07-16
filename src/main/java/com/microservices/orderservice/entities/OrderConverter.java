package com.microservices.orderservice.entities;

import com.microservices.orderservice.model.Order;
import com.microservices.orderservice.model.PaymentMode;

import java.time.Instant;

public class OrderConverter {

    public static OrderEntity convertToEntity(Order order) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setProductId(order.getProductId());
        orderEntity.setQuantity(order.getQuantity());
        orderEntity.setOrderDate(Instant.now());
        orderEntity.setOrderStatus("CREATED");
        orderEntity.setAmount(order.getAmount());
        return orderEntity;
    }

    public static Order convertFromEntity(OrderEntity orderEntity) {
        Order order = new Order();
        order.setOrderId(orderEntity.getOrderId());
        order.setProductId(orderEntity.getProductId());
        order.setQuantity(orderEntity.getQuantity());
        order.setOrderDate(orderEntity.getOrderDate());
        order.setOrderStatus(orderEntity.getOrderStatus());
        order.setAmount(orderEntity.getAmount());
        order.setPaymentMode(PaymentMode.APPLE_PAY);
        return order;
    }
}
