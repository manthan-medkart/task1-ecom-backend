package com.task.e_commerce.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItemResponseDto {

    private Long id;
    private Long quantity;
    private Double price;
    private Long productId;
    private String productName;
}
