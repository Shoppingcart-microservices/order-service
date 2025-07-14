package com.microservices.orderservice.model;

public enum PaymentMode {

    CASH(1, "Cash"),
    PAYPAL(2, "Paypal"),
    DEBIT_CARD(3, "Debit Card"),
    CREDIT_CARD(4, "Credit Card"),
    APPLE_PAY(5, "Apple Pay");

    private final int code;
    private final String description;

    PaymentMode(int code, String description) {
        this.code = code;
        this.description = description;
    }
}
