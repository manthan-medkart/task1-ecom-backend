package com.task.e_commerce.controllers;

import com.task.e_commerce.dtos.CartDto;
import com.task.e_commerce.entities.UserEntity;
import com.task.e_commerce.services.CartService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public CartDto getCart() {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return cartService.getCart(user.getId());
    }


}
