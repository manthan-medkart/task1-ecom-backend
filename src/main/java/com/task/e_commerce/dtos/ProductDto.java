package com.task.e_commerce.dtos;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    private Long id;
    private Long productCode;
    private String name;
    private String composition;
    private Long mrp;
    private Long salesRate;
    private Long totalStrip;
    private Long medicinePerStrip;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
