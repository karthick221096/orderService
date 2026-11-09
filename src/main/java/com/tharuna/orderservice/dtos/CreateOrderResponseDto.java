package com.tharuna.orderservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrderResponseDto {
    private Long orderId;
    private String paymentLinkId;
    private String paymentLinkUrl;
    private String status;
}
