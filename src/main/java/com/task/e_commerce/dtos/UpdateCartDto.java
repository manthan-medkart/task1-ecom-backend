package com.task.e_commerce.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateCartDto {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Quantity is required")
    private Long quantity;
}
