package com.task.e_commerce.services;

import com.task.e_commerce.dtos.OrderItemResponseDto;
import com.task.e_commerce.dtos.OrderRequestDto;
import com.task.e_commerce.dtos.OrderResponseDto;
import com.task.e_commerce.entities.*;
import com.task.e_commerce.entities.enums.OrderStatus;
import com.task.e_commerce.exceptions.ResourceNotFoundException;
import com.task.e_commerce.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemsRepository orderItemsRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository,
                        OrderItemsRepository orderItemsRepository,
                        CartRepository cartRepository,
                        CartItemRepository cartItemRepository,
                        UserRepository userRepository,
                        ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderItemsRepository = orderItemsRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public OrderResponseDto placeOrder(Long userId, OrderRequestDto orderRequestDto) {
        // 1. Get user's active cart
        CartEntity cartEntity = cartRepository.findByUserEntityIdAndActive(userId, true);
        if (cartEntity == null) {
            throw new IllegalArgumentException("Cannot place order: No active cart found.");
        }

        // 2. Fetch cart items
        List<CartItemEntity> cartItems = cartItemRepository.findAllByCartEntityId(cartEntity.getId());
        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cannot place order: Cart is empty.");
        }

        // 3. Verify stock availability for each item
        for (CartItemEntity cartItem : cartItems) {
            ProductEntity product = cartItem.getProductEntity();
            long availableStock = product.getTotalStrip() != null ? product.getTotalStrip() : 0L;
            if (cartItem.getQuantity() > availableStock) {
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getName() 
                        + ". Available: " + availableStock + ", Requested: " + cartItem.getQuantity());
            }
        }

        // 4. Calculate total price
        double totalPrice = cartItems.stream()
                .mapToDouble(cartItem -> cartItem.getProductEntity().getSalesRate() * cartItem.getQuantity())
                .sum();

        // 5. Create Order
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id: " + userId + " does not exist."));

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setUserEntity(user);
        orderEntity.setOrderStatus(OrderStatus.PENDING);
        orderEntity.setTotalPrice(totalPrice);
        orderEntity.setOrderDate(LocalDateTime.now());
        orderEntity.setShippingAddress(orderRequestDto.getShippingAddress());
        orderEntity.setPaymentMethod(orderRequestDto.getPaymentMethod());

        orderEntity = orderRepository.save(orderEntity);

        // 6. Create Order Items & Deduct Stock
        List<OrderItemResponseDto> orderItemResponseDtos = new ArrayList<>();
        for (CartItemEntity cartItem : cartItems) {
            ProductEntity product = cartItem.getProductEntity();
            
            // Create OrderItem
            OrderItemsEntity orderItem = new OrderItemsEntity();
            orderItem.setOrderEntity(orderEntity);
            orderItem.setProductEntity(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(product.getSalesRate());
            
            orderItem = orderItemsRepository.save(orderItem);

            // Deduct Stock
            product.setTotalStrip(product.getTotalStrip() - cartItem.getQuantity());
            productRepository.save(product);

            // Map to DTO
            OrderItemResponseDto itemDto = new OrderItemResponseDto();
            itemDto.setId(orderItem.getId());
            itemDto.setQuantity(orderItem.getQuantity());
            itemDto.setPrice(orderItem.getPrice());
            itemDto.setProductId(product.getId());
            itemDto.setProductName(product.getName());
            orderItemResponseDtos.add(itemDto);
        }

        // 7. Deactivate Cart
        cartEntity.setActive(false);
        cartEntity.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cartEntity);

        // 8. Map Order to Response DTO
        OrderResponseDto responseDto = new OrderResponseDto();
        responseDto.setId(orderEntity.getId());
        responseDto.setOrderStatus(orderEntity.getOrderStatus());
        responseDto.setTotalPrice(orderEntity.getTotalPrice());
        responseDto.setOrderDate(orderEntity.getOrderDate());
        responseDto.setShippingAddress(orderEntity.getShippingAddress());
        responseDto.setPaymentMethod(orderEntity.getPaymentMethod());
        responseDto.setOrderItems(orderItemResponseDtos);

        return responseDto;
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<OrderResponseDto> getUserOrders(Long userId) {
        List<OrderEntity> orders = orderRepository.findAllByUserEntityIdOrderByOrderDateDesc(userId);
        List<OrderResponseDto> responseDtos = new ArrayList<>();

        for (OrderEntity order : orders) {
            OrderResponseDto responseDto = new OrderResponseDto();
            responseDto.setId(order.getId());
            responseDto.setOrderStatus(order.getOrderStatus());
            responseDto.setTotalPrice(order.getTotalPrice());
            responseDto.setOrderDate(order.getOrderDate());
            responseDto.setShippingAddress(order.getShippingAddress());
            responseDto.setPaymentMethod(order.getPaymentMethod());

            List<OrderItemResponseDto> orderItemResponseDtos = new ArrayList<>();
            if (order.getOrderItems() != null) {
                for (OrderItemsEntity orderItem : order.getOrderItems()) {
                    OrderItemResponseDto itemDto = new OrderItemResponseDto();
                    itemDto.setId(orderItem.getId());
                    itemDto.setQuantity(orderItem.getQuantity());
                    itemDto.setPrice(orderItem.getPrice());
                    if (orderItem.getProductEntity() != null) {
                        itemDto.setProductId(orderItem.getProductEntity().getId());
                        itemDto.setProductName(orderItem.getProductEntity().getName());
                    }
                    orderItemResponseDtos.add(itemDto);
                }
            }
            responseDto.setOrderItems(orderItemResponseDtos);
            responseDtos.add(responseDto);
        }

        return responseDtos;
    }
}
