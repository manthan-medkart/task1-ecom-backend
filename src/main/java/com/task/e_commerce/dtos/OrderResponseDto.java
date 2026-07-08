package com.task.e_commerce.dtos;

import com.task.e_commerce.entities.enums.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponseDto {

    private Long id;
    private OrderStatus orderStatus;
    private Double totalPrice;
    private LocalDateTime orderDate;
    private String shippingAddress;
    private String paymentMethod;

}
