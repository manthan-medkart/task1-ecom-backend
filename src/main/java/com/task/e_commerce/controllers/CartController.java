package com.task.e_commerce.controllers;

import com.task.e_commerce.dtos.CartDto;
import com.task.e_commerce.dtos.AddToCartDto;
import com.task.e_commerce.dtos.UpdateCartDto;
import com.task.e_commerce.entities.UserEntity;
import com.task.e_commerce.services.CartService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<CartDto> getCart() {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new ResponseEntity<>(cartService.getCart(user.getId()), HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<CartDto> addProductToCart(@RequestBody @Valid AddToCartDto addToCartDto) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new ResponseEntity<>(cartService.addProductToCart(user.getId(), addToCartDto), HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<CartDto> updateProductInCart(@RequestBody @Valid UpdateCartDto updateCartDto) {

        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new ResponseEntity<>(cartService.updateProductInCart(user.getId(), updateCartDto), HttpStatus.OK) ;
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<CartDto> removeProductFromCart(@PathVariable Long productId) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new ResponseEntity<>(cartService.removeProductFromCart(user.getId(), productId), HttpStatus.OK);
    }

    @PostMapping("/clear")
    public ResponseEntity<CartDto> clearCart() {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new ResponseEntity<>(cartService.clearCart(user.getId()), HttpStatus.OK);
    }
}
