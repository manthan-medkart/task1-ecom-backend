package com.task.e_commerce.dtos;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    private Long id;
    private String name;
    private String description;
    private Long price;
    private String category;
    private Long stockQuantity;
    private String imageUrl;



}
