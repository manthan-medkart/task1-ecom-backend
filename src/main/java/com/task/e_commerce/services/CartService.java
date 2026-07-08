package com.task.e_commerce.services;

import com.task.e_commerce.dtos.CartDto;
import com.task.e_commerce.dtos.CartItemsDto;
import com.task.e_commerce.dtos.AddToCartDto;
import com.task.e_commerce.dtos.UpdateCartDto;
import com.task.e_commerce.entities.CartEntity;
import com.task.e_commerce.entities.CartItemEntity;
import com.task.e_commerce.entities.ProductEntity;
import com.task.e_commerce.entities.UserEntity;
import com.task.e_commerce.exceptions.ResourceNotFoundException;
import com.task.e_commerce.repositories.CartItemRepository;
import com.task.e_commerce.repositories.CartRepository;
import com.task.e_commerce.repositories.ProductRepository;
import com.task.e_commerce.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private final ModelMapper modelMapper;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public CartService(CartItemRepository cartItemRepository, CartRepository cartRepository, ModelMapper modelMapper, UserRepository userRepository, ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.cartRepository = cartRepository;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    private CartEntity getOrCreateActiveCart(Long userId) {
        CartEntity cartEntity = cartRepository.findByUserEntityIdAndActive(userId, true);
        if (cartEntity == null) {
            cartEntity = new CartEntity();
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User with id: " + userId + " does not exist."));
            cartEntity.setUserEntity(user);
            cartEntity.setActive(true);
            cartEntity.setCreatedAt(LocalDateTime.now());
            cartEntity.setUpdatedAt(LocalDateTime.now());
            cartEntity = cartRepository.save(cartEntity);
        }
        return cartEntity;
    }

    public CartDto getCart(Long id) {

        CartEntity cartEntity = getOrCreateActiveCart(id);

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

    public CartDto addProductToCart(Long userId, AddToCartDto addToCartDto) {
        CartEntity cartEntity = getOrCreateActiveCart(userId);

        ProductEntity product = productRepository.findById(addToCartDto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product with id: " + addToCartDto.getProductId() + " does not exist."));

        Optional<CartItemEntity> existingCartItemOpt = cartItemRepository.findByCartEntityIdAndProductEntityId(cartEntity.getId(), product.getId());

        long currentQuantity = existingCartItemOpt.map(CartItemEntity::getQuantity).orElse(0L);
        long targetQuantity = currentQuantity + addToCartDto.getQuantity();

        // Enforce Stock Check
        long availableStock = product.getTotalStrip() != null ? product.getTotalStrip() : 0L;
        if (targetQuantity > availableStock) {
            throw new IllegalArgumentException("Cannot add product. Requested quantity (" + targetQuantity + ") exceeds available stock (" + availableStock + ").");
        }

        CartItemEntity cartItemEntity;
        if (existingCartItemOpt.isPresent()) {
            cartItemEntity = existingCartItemOpt.get();
            cartItemEntity.setQuantity(targetQuantity);
        } else {
            cartItemEntity = new CartItemEntity();
            cartItemEntity.setCartEntity(cartEntity);
            cartItemEntity.setProductEntity(product);
            cartItemEntity.setQuantity(targetQuantity);
            cartItemEntity.setPrice(product.getSalesRate());
        }

        cartItemRepository.save(cartItemEntity);

        cartEntity.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cartEntity);

        return getCart(userId);
    }

    public CartDto updateProductInCart(Long userId, UpdateCartDto updateCartDto) {
        CartEntity cartEntity = getOrCreateActiveCart(userId);

        ProductEntity product = productRepository.findById(updateCartDto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product with id: " + updateCartDto.getProductId() + " does not exist."));

        Optional<CartItemEntity> existingCartItemOpt = cartItemRepository.findByCartEntityIdAndProductEntityId(cartEntity.getId(), product.getId());

        long targetQuantity = updateCartDto.getQuantity();

        // If target quantity <= 0, remove product from cart
        if (targetQuantity <= 0) {
            if (existingCartItemOpt.isPresent()) {
                cartItemRepository.delete(existingCartItemOpt.get());
            }
        } else {
            // Enforce Stock Check
            long availableStock = product.getTotalStrip() != null ? product.getTotalStrip() : 0L;
            if (targetQuantity > availableStock) {
                throw new IllegalArgumentException("Cannot update quantity. Requested quantity (" + targetQuantity + ") exceeds available stock (" + availableStock + ").");
            }

            CartItemEntity cartItemEntity;
            if (existingCartItemOpt.isPresent()) {
                cartItemEntity = existingCartItemOpt.get();
                cartItemEntity.setQuantity(targetQuantity);
            } else {
                cartItemEntity = new CartItemEntity();
                cartItemEntity.setCartEntity(cartEntity);
                cartItemEntity.setProductEntity(product);
                cartItemEntity.setQuantity(targetQuantity);
                cartItemEntity.setPrice(product.getSalesRate());
            }
            cartItemRepository.save(cartItemEntity);
        }

        cartEntity.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cartEntity);

        return getCart(userId);
    }

    public CartDto removeProductFromCart(Long userId, Long productId) {
        CartEntity cartEntity = getOrCreateActiveCart(userId);

        CartItemEntity cartItemEntity = cartItemRepository.findByCartEntityIdAndProductEntityId(cartEntity.getId(), productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id: " + productId + " is not in your cart."));

        cartItemRepository.delete(cartItemEntity);

        cartEntity.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cartEntity);

        return getCart(userId);
    }

    public CartDto clearCart(Long userId) {
        CartEntity cartEntity = getOrCreateActiveCart(userId);

        List<CartItemEntity> cartItems = cartItemRepository.findAllByCartEntityId(cartEntity.getId());
        cartItemRepository.deleteAll(cartItems);

        cartEntity.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cartEntity);

        return getCart(userId);
    }
}
