package com.tharuna.orderservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order extends BaseModel {
    private Long productId;
    private String productName;
    private Double price;
    private Integer quantity;
    private Long totalAmount;
    private String email;
    private String phoneNumber;
    private String paymentLinkId;
    private String paymentLinkUrl;

    @Enumerated(EnumType.STRING)
    private PaymentGateway gateway;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;
}
