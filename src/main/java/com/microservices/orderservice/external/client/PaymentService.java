package com.microservices.orderservice.external.client;

import com.microservices.orderservice.model.Payment;
import com.microservices.orderservice.model.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "paymentService/api/v1/payment", fallback = ExternalService.class)
public interface PaymentService {

    @PostMapping
    ResponseEntity<Long> doPayment(@RequestBody Payment payment);

    @GetMapping("/order/{id}")
    ResponseEntity<PaymentResponse> getPaymentDetailsByOrderId(@PathVariable("id") long orderId);
}
