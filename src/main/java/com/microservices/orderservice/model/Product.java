package com.microservices.orderservice.model;

import lombok.Data;

@Data
public class Product {
    private Long productId;
    private String productName;
    private long price;
    private long quantity;
}
