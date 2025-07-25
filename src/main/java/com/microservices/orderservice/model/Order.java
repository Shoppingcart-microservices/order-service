package com.microservices.orderservice.model;

import lombok.Data;

import java.time.Instant;

@Data
public class Order {
    private long orderId;
    private long productId;
    private long quantity;
    private Instant orderDate;
    private String orderStatus;
    private long amount;
    private PaymentMode paymentMode;
}
