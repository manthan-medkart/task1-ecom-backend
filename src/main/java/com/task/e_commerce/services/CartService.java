package com.task.e_commerce.services;

import com.task.e_commerce.dtos.CartDto;
import com.task.e_commerce.dtos.CartItemsDto;
import com.task.e_commerce.entities.CartEntity;
import com.task.e_commerce.entities.CartItemEntity;
import com.task.e_commerce.repositories.CartItemRepository;
import com.task.e_commerce.repositories.CartRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    private final ModelMapper modelMapper;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public CartService(CartItemRepository cartItemRepository, CartRepository cartRepository, ModelMapper modelMapper) {
        this.cartItemRepository = cartItemRepository;
        this.cartRepository = cartRepository;
        this.modelMapper = modelMapper;
    }

    public CartDto getCart(Long id) {

        CartEntity cartEntity = cartRepository.findByUserEntityIdAndActive(id, true);

        List<CartItemEntity> cartItems = cartItemRepository.findAllByCartEntityId(cartEntity.getId());

        Long totalPrice = cartItems.stream()
                .mapToLong(cartItem ->
                        cartItem.getProductEntity().getSalesRate()
                                * cartItem.getQuantity()
                )
                .sum();

        return new CartDto(cartItems
                .stream()
                .map(cartItem -> modelMapper.map(cartItem, CartItemsDto.class))
                .toList()
                , totalPrice);

    }
}
