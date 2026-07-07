package com.task.e_commerce.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDto {

    private List<CartItemsDto> items;
    private Long totalPrice;

}
