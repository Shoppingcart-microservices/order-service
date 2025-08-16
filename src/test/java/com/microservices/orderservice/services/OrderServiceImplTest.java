package com.microservices.orderservice.services;

import com.microservices.orderservice.entities.OrderConverter;
import com.microservices.orderservice.entities.OrderEntity;
import com.microservices.orderservice.exception.OrderServiceCustomException;
import com.microservices.orderservice.external.client.PaymentService;
import com.microservices.orderservice.external.client.ProductService;
import com.microservices.orderservice.model.*;
import com.microservices.orderservice.repositories.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductService productService;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    OrderServiceImpl orderService;

    @BeforeAll
    static void beforeAll() {

    }

    @Test
    @DisplayName("Get all orders - Success")
    void testGetAllOrders() {
        when(orderRepository.findAll()).thenReturn(List.of());
        orderService.getAllOrders();

        verify(orderRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Get order by id - Success")
    void testGetOrderDetailsById() {
        long orderId = 1;
        //Mocking
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderId(orderId);
        orderEntity.setQuantity(100);
        orderEntity.setProductId(50);

        Product product = new Product();
        product.setProductId(orderEntity.getProductId());
        product.setProductName("Test Product");
        product.setPrice(100);

        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setOrderId(orderId);
        paymentResponse.setPaymentMode(PaymentMode.DEBIT_CARD);

        Mockito.when(orderRepository.findById(anyLong())).thenReturn(Optional.of(orderEntity));
        Mockito.when(productService.getProductById(orderEntity.getProductId())).thenReturn(ResponseEntity.ok(product));
        Mockito.when(paymentService.getPaymentDetailsByOrderId(orderEntity.getOrderId())).thenReturn(ResponseEntity.ok(paymentResponse));
        //Actual
        OrderResponse orderResponse = orderService.getOrderById(orderId);
        //Verifications
        verify(orderRepository, times(1)).findById(orderId);
        verify(productService, times(1)).getProductById(orderEntity.getProductId());
        verify(paymentService, times(1)).getPaymentDetailsByOrderId(orderEntity.getOrderId());
        //Assertions
        Assertions.assertNotNull(orderResponse);
        Assertions.assertEquals(orderId, orderEntity.getOrderId());
    }

    @Test
    @DisplayName("Get order by id - Failure")
    void testGetOrderDetailsByIdFailure() {
        Mockito.when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());
        long orderId = 1;

        RuntimeException runtimeException = Assertions.assertThrows(RuntimeException.class, () -> orderService.getOrderById(orderId));
        Assertions.assertEquals("Order with id " + orderId + " not found" ,  runtimeException.getMessage());

        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    @DisplayName("Place Order - Success")
    void testPlaceOrder() {
        Order order = new Order();
        order.setOrderId(1);
        order.setProductId(1);
        order.setQuantity(10);
        order.setPaymentMode(PaymentMode.CASH);
        order.setAmount(100);

        OrderEntity orderEntity = OrderConverter.convertToEntity(order);
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(orderEntity);
        when(productService.reduceQuantity(anyLong(), anyLong())).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        when(paymentService.doPayment(any(Payment.class))).thenReturn(new ResponseEntity<>(1L,HttpStatus.OK));

        long orderId = orderService.createOrder(order);

        verify(orderRepository, times(2)).save(any(OrderEntity.class));
        verify(productService, times(1)).reduceQuantity(anyLong(), anyLong());
        verify(paymentService, times(1)).doPayment(any(Payment.class));

        Assertions.assertEquals(order.getOrderId(), orderId);
    }

    @Test
    @DisplayName("Place Order - Failure")
    void testPlaceOrderFailure() {
        Order order = new Order();
        order.setOrderId(1);
        order.setProductId(1);
        order.setQuantity(10);
        order.setPaymentMode(PaymentMode.CASH);
        order.setAmount(100);

        OrderEntity orderEntity = OrderConverter.convertToEntity(order);
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(orderEntity);
        when(productService.reduceQuantity(anyLong(), anyLong())).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        when(paymentService.doPayment(any(Payment.class))).thenThrow(new OrderServiceCustomException());

        long orderId = orderService.createOrder(order);

        verify(orderRepository, times(2)).save(any(OrderEntity.class));
        verify(productService, times(1)).reduceQuantity(anyLong(), anyLong());
        verify(paymentService, times(1)).doPayment(any(Payment.class));

        Assertions.assertEquals(order.getOrderId(), orderId);
    }
}