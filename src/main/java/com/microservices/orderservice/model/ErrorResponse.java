package com.microservices.orderservice.model;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ErrorResponse {
    private String errorMessage;
    private int errorCode;
    private HttpStatus status;
}
