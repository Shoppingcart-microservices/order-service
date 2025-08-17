package com.microservices.orderservice.controllers;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.microservices.orderservice.entities.OrderEntity;
import com.microservices.orderservice.external.client.PaymentService;
import com.microservices.orderservice.external.client.ProductService;
import com.microservices.orderservice.model.Order;
import com.microservices.orderservice.model.PaymentMode;
import com.microservices.orderservice.repositories.OrderRepository;
import com.microservices.orderservice.services.OrderService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Instant;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderService orderService;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private PaymentService paymentService;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void test_WhenPlaceOrder_DoPayment_Success() throws Exception {

        Order order = new Order();
        order.setOrderDate(Instant.now());
        order.setProductId(1);
        order.setQuantity(10);
        order.setOrderStatus("PAYMENT_COMPLETED");
        order.setPaymentMode(PaymentMode.CASH);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/order")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_Customer")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        String orderId = mvcResult.getResponse().getContentAsString();

        Optional<OrderEntity> orderResult = orderRepository.findById(Long.parseLong(orderId));
        Assertions.assertTrue(orderResult.isPresent());
        OrderEntity o = orderResult.get();
        Assertions.assertEquals(Long.parseLong(orderId), o.getOrderId());
        Assertions.assertEquals(order.getOrderStatus(), o.getOrderStatus());
        Assertions.assertEquals(order.getProductId(), o.getProductId());
        Assertions.assertEquals(order.getQuantity(), o.getQuantity());
        Assertions.assertEquals(order.getAmount(), o.getAmount());
    }

    @AfterEach
    void afterEach() {
        orderRepository.deleteAll();
    }

    private void clearAllData() {
        orderRepository.deleteAll();
    }

    private final ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

}