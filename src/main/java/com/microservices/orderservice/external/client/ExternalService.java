package com.microservices.orderservice.external.client;

import com.microservices.orderservice.exception.OrderServiceCustomException;
import com.microservices.orderservice.model.Payment;
import com.microservices.orderservice.model.PaymentResponse;
import com.microservices.orderservice.model.Product;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class ExternalService implements ProductService, PaymentService {

    @Override
    public ResponseEntity<Void> reduceQuantity(long productId, long quantity) {
        throw new OrderServiceCustomException("Product Service not available", 503, HttpStatus.SERVICE_UNAVAILABLE.value());
    }

    @Override
    public ResponseEntity<Product> getProductById(long productId) {
        throw new OrderServiceCustomException("Product Service not available getProductById", 503, HttpStatus.SERVICE_UNAVAILABLE.value());
    }

    @Override
    public ResponseEntity<Long> doPayment(Payment payment) {
        throw new OrderServiceCustomException("Payment Service not available", 503, HttpStatus.SERVICE_UNAVAILABLE.value());
    }

    @Override
    public ResponseEntity<PaymentResponse> getPaymentDetailsByOrderId(long orderId) {
        throw new OrderServiceCustomException("Payment Service not available DetailsByOrderId", 503, HttpStatus.SERVICE_UNAVAILABLE.value());
    }
}
