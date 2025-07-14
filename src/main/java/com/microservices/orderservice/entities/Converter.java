package com.microservices.orderservice.entities;

import com.microservices.orderservice.model.Order;
import com.microservices.orderservice.model.PaymentMode;

import java.time.Instant;

public class Converter {

    public static OrderEntity convertToEntity(Order order) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderDate(Instant.now());
//        orderEntity.setOrderStatus(order.getOrderStatus());
        orderEntity.setQuantity(order.getQuantity());
//        orderEntity.setAmount(order.getAmount());
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
