package com.tharuna.orderservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentResponseDto {
    private String paymentLinkId;
    private String paymentLinkUrl;
    private String gateway;
    private String orderId;
}
