package com.tharuna.orderservice.service;

import com.tharuna.orderservice.dtos.*;
import com.tharuna.orderservice.model.Order;
import com.tharuna.orderservice.model.OrderStatus;
import com.tharuna.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;

    @Value("${product.service.url}")
    private String productServiceUrl;

    @Value("${payment.service.url}")
    private String paymentServiceUrl;

    public OrderService(OrderRepository orderRepository, RestTemplate restTemplate) {
        this.orderRepository = orderRepository;
        this.restTemplate = restTemplate;
    }

    public CreateOrderResponseDto createOrder(CreateOrderRequestDto request) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", request.getToken());

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<ProductResponseDto> productResponse = restTemplate.exchange(
                productServiceUrl + "/product/" + request.getProductId(),
                HttpMethod.GET,
                entity,
                ProductResponseDto.class
        );

        ProductResponseDto product = productResponse.getBody();
        if (product == null) {
            throw new RuntimeException("Product not found: " + request.getProductId());
        }

        long totalAmount = Math.round(product.getPrice() * request.getQuantity() * 100);

        Order order = new Order();
        order.setProductId(request.getProductId());
        order.setProductName(product.getTitle());
        order.setPrice(product.getPrice());
        order.setQuantity(request.getQuantity());
        order.setTotalAmount(totalAmount);
        order.setEmail(request.getEmail());
        order.setPhoneNumber(request.getPhoneNumber());
        order.setGateway(request.getGateway());
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(new Date());
        order.setUpdatedAt(new Date());

        order = orderRepository.save(order);

        PaymentRequestDto paymentRequest = new PaymentRequestDto();
        paymentRequest.setOrderId(String.valueOf(order.getId()));
        paymentRequest.setEmail(request.getEmail());
        paymentRequest.setPhoneNumber(request.getPhoneNumber());
        paymentRequest.setAmount(totalAmount);
        paymentRequest.setCurrency(request.getGateway().name().equals("RAZORPAY") ? "INR" : "USD");
        paymentRequest.setProductName(product.getTitle());
        paymentRequest.setGateway(request.getGateway().name());

        PaymentResponseDto paymentResponse = restTemplate.postForObject(
                paymentServiceUrl + "/payment/initiate",
                paymentRequest,
                PaymentResponseDto.class
        );

        if (paymentResponse == null) {
            throw new RuntimeException("Payment service did not respond");
        }

        order.setPaymentLinkId(paymentResponse.getPaymentLinkId());
        order.setPaymentLinkUrl(paymentResponse.getPaymentLinkUrl());
        order.setUpdatedAt(new Date());
        orderRepository.save(order);

        CreateOrderResponseDto response = new CreateOrderResponseDto();
        response.setOrderId(order.getId());
        response.setPaymentLinkId(paymentResponse.getPaymentLinkId());
        response.setPaymentLinkUrl(paymentResponse.getPaymentLinkUrl());
        response.setStatus(order.getStatus().name());

        return response;
    }

    public void confirmOrder(Long orderId) {
        orderRepository.findById(orderId).ifPresent(order -> {
            order.setStatus(OrderStatus.CONFIRMED);
            order.setUpdatedAt(new Date());
            orderRepository.save(order);

            restTemplate.getForObject(
                    paymentServiceUrl + "/payment/success?orderId=" + orderId,
                    String.class
            );
        });
    }
}
