package com.task.e_commerce.dtos;

import lombok.Data;

@Data
public class CartItemsDto {

    private Long id;
    private ProductDto product;
    private Long quantity;
    private Long price;

}
