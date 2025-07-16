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
        orderEntity.setAmount(order.getTotalAmount());
        return orderEntity;
    }

    public static Order convertFromEntity(OrderEntity orderEntity) {
        Order product = new Order();
        product.setProductId(orderEntity.getProductId());
        product.setTotalAmount(orderEntity.getAmount());
        product.setQuantity(orderEntity.getQuantity());
        product.setPaymentMode(PaymentMode.APPLE_PAY);
        return product;
    }
}
