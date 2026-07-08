package com.task.e_commerce.controllers;

import com.task.e_commerce.dtos.CartDto;
import com.task.e_commerce.dtos.AddToCartDto;
import com.task.e_commerce.dtos.UpdateCartDto;
import com.task.e_commerce.entities.UserEntity;
import com.task.e_commerce.services.CartService;
import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/add")
    public CartDto addProductToCart(@RequestBody @Valid AddToCartDto addToCartDto) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return cartService.addProductToCart(user.getId(), addToCartDto);
    }

    @PutMapping("/update")
    public CartDto updateProductInCart(@RequestBody @Valid UpdateCartDto updateCartDto) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return cartService.updateProductInCart(user.getId(), updateCartDto);
    }

    @DeleteMapping("/remove/{productId}")
    public CartDto removeProductFromCart(@PathVariable Long productId) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return cartService.removeProductFromCart(user.getId(), productId);
    }

    @PostMapping("/clear")
    public CartDto clearCart() {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return cartService.clearCart(user.getId());
    }
}
