package com.microservices.orderservice.exception;

import lombok.Data;

@Data
public class OrderServiceCustomException extends RuntimeException {

    private final int errorCode;
    private int statusCode;

    public OrderServiceCustomException(String message, int errorCode, int statusCode) {
        super(message);
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }
}
