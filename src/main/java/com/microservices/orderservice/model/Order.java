package com.microservices.orderservice.model;

import lombok.Data;

@Data
public class Order {
    private long productId;
    private long totalAmount;
    private long quantity;
    private PaymentMode paymentMode;
}
