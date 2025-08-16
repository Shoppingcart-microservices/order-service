package com.microservices.orderservice.exception;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderServiceCustomException extends RuntimeException {

    private int errorCode;
    private int statusCode;

    public OrderServiceCustomException(String message, int errorCode, int statusCode) {
        super(message);
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }
}
