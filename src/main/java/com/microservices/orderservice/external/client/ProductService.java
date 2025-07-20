package com.microservices.orderservice.external.client;

import com.microservices.orderservice.model.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "ProductService/api/v1/product/", fallback = ExternalService.class)
public interface ProductService {

    @PutMapping("reduceQuantity/{id}")
    ResponseEntity<Void> reduceQuantity(@PathVariable("id") long productId, @RequestParam long quantity);

    @GetMapping("{id}")
    ResponseEntity<Product> getProductById(@PathVariable("id") long productId);
}
