package com.task.e_commerce.controllers;

import com.task.e_commerce.dtos.OrderRequestDto;
import com.task.e_commerce.dtos.OrderResponseDto;
import com.task.e_commerce.entities.UserEntity;
import com.task.e_commerce.services.OrderService;
import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public OrderResponseDto placeOrder(@RequestBody @Valid OrderRequestDto orderRequestDto) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return orderService.placeOrder(user.getId(), orderRequestDto);
    }

    @GetMapping
    public List<OrderResponseDto> getUserOrders() {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return orderService.getUserOrders(user.getId());
    }
}
