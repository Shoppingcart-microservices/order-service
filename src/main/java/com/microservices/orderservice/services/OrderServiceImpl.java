package com.microservices.orderservice.services;

import com.microservices.orderservice.entities.OrderConverter;
import com.microservices.orderservice.entities.OrderEntity;
import com.microservices.orderservice.external.client.PaymentService;
import com.microservices.orderservice.external.client.ProductService;
import com.microservices.orderservice.model.*;
import com.microservices.orderservice.repositories.OrderRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final PaymentService paymentService;
    private final RestTemplate restTemplate;

    public OrderServiceImpl(OrderRepository orderRepository, ProductService productService,
                            PaymentService paymentService, RestTemplate restTemplate) {
        this.orderRepository = orderRepository;
        this.productService = productService;
        this.paymentService = paymentService;
        this.restTemplate = restTemplate;
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
        Payment payment = new Payment(orderEntity.getOrderId(), order.getAmount(), "12345", order.getPaymentMode());
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

    @Override
    public OrderResponse getOrderById(long orderId) {
        Order order = orderRepository.findById(orderId)
                .map(OrderConverter::convertFromEntity)
                .orElseThrow(() -> new RuntimeException("Order with id " + orderId + " not found"));

        // Call Product service to fetch product info.
        log.info("Invoking Product Service to fetch the product.");
        Product product = restTemplate.getForObject("http://ProductService/api/v1/product/" + order.getProductId(), Product.class);
        OrderResponse.ProductDetails productDetails = new OrderResponse.ProductDetails();
        productDetails.setProductId(product.getProductId());
        productDetails.setProductName(product.getProductName());
        productDetails.setPrice(product.getPrice());
        productDetails.setQuantity(product.getQuantity());

        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setOrderId(order.getOrderId());
        orderResponse.setOrderDate(order.getOrderDate());
        orderResponse.setOrderStatus(order.getOrderStatus());
        orderResponse.setAmount(order.getAmount());
        orderResponse.setProductDetails(productDetails);

        // Call Payment service to fetch product info.
        log.info("Invoking Payment Service to fetch the payment details.");
        PaymentResponse paymentResponse = restTemplate.getForObject("http://paymentService/api/v1/payment/order/" + order.getOrderId(), PaymentResponse.class);
        OrderResponse.PaymentDetails paymentDetails = new OrderResponse.PaymentDetails();
        paymentDetails.setPaymentId(paymentResponse.getPaymentId());
        paymentDetails.setPaymentStatus(paymentResponse.getPaymentStatus());
        paymentDetails.setPaymentMode(paymentResponse.getPaymentMode());
        paymentDetails.setAmount(paymentResponse.getAmount());
        paymentDetails.setPaymentDate(paymentResponse.getPaymentDate());
        paymentDetails.setOrderId(order.getOrderId());
        orderResponse.setPaymentDetails(paymentDetails);

        return orderResponse;
    }
}
