package com.task.e_commerce.dtos;

import lombok.Data;

@Data
public class CartItemsDto {

    private Long id;
    private String product;
    private Long quantity;
    private Long price;

}
