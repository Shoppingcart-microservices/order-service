package com.microservices.orderservice.services;

import com.microservices.orderservice.entities.Converter;
import com.microservices.orderservice.entities.OrderEntity;
import com.microservices.orderservice.external.client.ProductService;
import com.microservices.orderservice.model.Order;
import com.microservices.orderservice.repositories.OrderRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;

    public OrderServiceImpl(OrderRepository orderRepository, ProductService productService) {
        this.orderRepository = orderRepository;
        this.productService = productService;
    }

    @Override
    public long createOrder(Order order) {
        // Order Entity -> save the order with status order created.
        // Call Product Service -> Block Products (reduce the quantity).
        // Payment Service -> Payments -> Success -> COMPLETE, else -> CANCELLED.
        OrderEntity orderEntity = Converter.convertToEntity(order);
        log.info("=> Adding order: {}", orderEntity);

        // Call the Product service to to reduce the quantity.
        productService.reduceQuantity(orderEntity.getProductId(), orderEntity.getQuantity());

        orderEntity = orderRepository.save(orderEntity);
        log.info("=> Order Added: {}", orderEntity);
        return orderEntity.getOrderId();
    }

    @Override
    public List<Order> getAllOrders() {
        List<OrderEntity> orderEntities = orderRepository.findAll();
        return orderEntities.stream()
                .map(Converter::convertFromEntity)
                .toList();
    }
}
