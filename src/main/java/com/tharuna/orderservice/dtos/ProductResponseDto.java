package com.tharuna.orderservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductResponseDto {
    private Long id;
    private String title;
    private Double price;
    private String description;
    private String imageUrl;
    private String categoryName;
}
