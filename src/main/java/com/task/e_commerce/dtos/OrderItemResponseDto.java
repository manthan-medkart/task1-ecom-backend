package com.task.e_commerce.dtos;

import lombok.Data;

@Data
public class OrderItemResponseDto {

    private Long id;
    private Long quantity;
    private Long price;
    private Long productId;
    private String productName;
}
