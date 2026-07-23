package com.task.e_commerce.controllers;

import com.task.e_commerce.dtos.OrderItemResponseDto;
import com.task.e_commerce.dtos.OrderRequestDto;
import com.task.e_commerce.dtos.OrderResponseDto;
import com.task.e_commerce.entities.UserEntity;
import com.task.e_commerce.entities.enums.OrderStatus;
import com.task.e_commerce.services.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<OrderResponseDto> placeOrder(@RequestBody @Valid OrderRequestDto orderRequestDto) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new ResponseEntity<>(orderService.placeOrder(user.getId(), orderRequestDto), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getUserOrders() {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new ResponseEntity<>(orderService.getUserOrders(user.getId()), HttpStatus.OK);
    }


    @GetMapping("/details/{orderId}")
    public ResponseEntity<List<OrderItemResponseDto>> getOrderDetails(@PathVariable Long orderId){
        return new ResponseEntity<>(orderService.getOrderDetails(orderId), HttpStatus.OK);
    }

    @PostMapping("/update-status/{orderId}")
    public ResponseEntity<OrderResponseDto> updateOrderStatus(@PathVariable Long orderId, @RequestParam OrderStatus status) {
        return new ResponseEntity<>(orderService.updateOrderStatus(orderId, status), HttpStatus.OK);
    }
}
