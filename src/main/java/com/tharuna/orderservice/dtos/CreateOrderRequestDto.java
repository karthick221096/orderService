package com.tharuna.orderservice.dtos;

import com.tharuna.orderservice.model.PaymentGateway;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrderRequestDto {
    private Long productId;
    private Integer quantity;
    private String email;
    private String phoneNumber;
    private PaymentGateway gateway;
    private String token;
}
