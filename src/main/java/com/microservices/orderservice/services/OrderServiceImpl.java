package com.microservices.orderservice.services;

import com.microservices.orderservice.entities.Converter;
import com.microservices.orderservice.entities.OrderEntity;
import com.microservices.orderservice.model.Order;
import com.microservices.orderservice.repositories.OrderRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService {

    OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Long createOrder(Order order) {
        OrderEntity orderEntity = Converter.convertToEntity(order);
        log.info("=> Adding order: {}", orderEntity);
        orderRepository.save(orderEntity);
        log.info("=> Order Added: {}", orderEntity);
        return orderEntity.getOrderId();
    }
}
