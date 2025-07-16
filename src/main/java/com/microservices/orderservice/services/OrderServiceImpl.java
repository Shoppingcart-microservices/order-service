package com.microservices.orderservice.services;

import com.microservices.orderservice.entities.OrderConverter;
import com.microservices.orderservice.entities.OrderEntity;
import com.microservices.orderservice.external.client.PaymentService;
import com.microservices.orderservice.external.client.ProductService;
import com.microservices.orderservice.model.Order;
import com.microservices.orderservice.model.Payment;
import com.microservices.orderservice.repositories.OrderRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final PaymentService paymentService;

    public OrderServiceImpl(OrderRepository orderRepository, ProductService productService, PaymentService paymentService) {
        this.orderRepository = orderRepository;
        this.productService = productService;
        this.paymentService = paymentService;
    }

    @Override
    public long createOrder(Order order) {
        // Order Entity -> save the order with status order created.
        // Call Product Service -> Block Products (reduce the quantity).
        // Payment Service -> Payments -> Success -> COMPLETE, else -> CANCELLED.
        OrderEntity orderEntity = OrderConverter.convertToEntity(order);
        log.info("=> Adding order: {}", orderEntity);

        // Call the Product service to to reduce the quantity.
        productService.reduceQuantity(orderEntity.getProductId(), orderEntity.getQuantity());

        orderEntity = orderRepository.save(orderEntity);

        // Call the Payment Service to do the payment.
        log.info("Calling Payment Service to complete the payment.");
        Payment payment = new Payment(orderEntity.getOrderId(), order.getTotalAmount(), "12345", order.getPaymentMode());
        String orderStatus;
        try {
            paymentService.doPayment(payment);
            log.info("Payment completed. Changing order status to PAYMENT_COMPLETED.");
            orderStatus = "PAYMENT_COMPLETED";
        } catch (Exception e) {
            log.info("Error during payment process. Changing order status to PAYMENT_FAILED.");
            orderStatus = "PAYMENT_FAILED";
        }
        // Update the order status
        orderEntity.setOrderStatus(orderStatus);
        orderRepository.save(orderEntity);

        log.info("=> Order Added: {}", orderEntity);
        return orderEntity.getOrderId();
    }

    @Override
    public List<Order> getAllOrders() {
        List<OrderEntity> orderEntities = orderRepository.findAll();
        return orderEntities.stream()
                .map(OrderConverter::convertFromEntity)
                .toList();
    }
}
