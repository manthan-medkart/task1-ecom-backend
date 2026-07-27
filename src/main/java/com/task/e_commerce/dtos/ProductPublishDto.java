package com.task.e_commerce.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductPublishDto {

    private Long id;
    private Long productCode;
    private String name;
    private String composition;
    private Double mrp;
    private Double sales_rate;
    private Long total_strip;
    private Long medicine_per_strip;
    private String image_url;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
