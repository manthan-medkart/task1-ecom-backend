package com.task.e_commerce.services;

import com.task.e_commerce.dtos.OrderItemResponseDto;
import com.task.e_commerce.dtos.OrderRequestDto;
import com.task.e_commerce.dtos.OrderResponseDto;
import com.task.e_commerce.entities.*;
import com.task.e_commerce.entities.enums.OrderStatus;
import com.task.e_commerce.exceptions.ResourceNotFoundException;
import com.task.e_commerce.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemsRepository orderItemsRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public OrderService(OrderRepository orderRepository,
            OrderItemsRepository orderItemsRepository,
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            UserRepository userRepository,
            ProductRepository productRepository, ModelMapper modelMapper) {
        this.orderRepository = orderRepository;
        this.orderItemsRepository = orderItemsRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public OrderResponseDto placeOrder(Long userId, OrderRequestDto orderRequestDto) {
        //get active cart of the user id passed
        CartEntity cartEntity = cartRepository.findByUserEntityIdAndActive(userId, true);

        //if found no active cart ---> means no cart Items in cart
        if (cartEntity == null) {
            throw new IllegalArgumentException("Cannot place order: No active cart found.");
        }

        //Fetch all Items related to that particular cartId
        List<CartItemEntity> cartItems = cartItemRepository.findAllByCartEntityId(cartEntity.getId());
        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cannot place order: Cart is empty.");
        }

        //This for loop basically check for the available stock of that particular product
        for (CartItemEntity cartItem : cartItems) {

            ProductEntity product = cartItem.getProductEntity();

            //Fetch stock from the database
            long availableStock;
            if (product.getTotalStrip() != null) {
                availableStock = product.getTotalStrip(); 
            }else {
                availableStock = 0L;
            }

            //If stock is not available ---> throw exception and message
            if (cartItem.getQuantity() > availableStock) {
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getName()
                        + ". Available: " + availableStock + ", Requested: " + cartItem.getQuantity());
            }
        }

        double totalPrice = cartItems.stream()
                .mapToDouble(cartItem -> cartItem.getProductEntity().getSalesRate() * cartItem.getQuantity())
                .sum();

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id: " + userId + " does not exist."));

        OrderEntity orderEntity = OrderEntity.builder()
                .userEntity(user)
                .orderStatus(OrderStatus.PENDING)
                .totalPrice(totalPrice)
                .orderDate(LocalDateTime.now())
                .shippingAddress(orderRequestDto.getShippingAddress())
                .paymentMethod(orderRequestDto.getPaymentMethod())
                .build();

        orderRepository.save(orderEntity);

        //Enter all the items that has been ordered into OrderItems
        for (CartItemEntity cartItem : cartItems) {
            ProductEntity product = cartItem.getProductEntity();
            OrderItemsEntity orderItem = OrderItemsEntity.builder()
                    .quantity(cartItem.getQuantity())
                    .price(cartItem.getPrice())
                    .productEntity(product)
                    .orderEntity(orderEntity)
                    .build();

            orderItemsRepository.save(orderItem);

            product.setTotalStrip(product.getTotalStrip() - cartItem.getQuantity());
            productRepository.save(product);

        }

        //Deactivate the cart
        cartEntity.setActive(false);
        cartEntity.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cartEntity);

        return modelMapper.map(orderEntity, OrderResponseDto.class);

    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<OrderResponseDto> getUserOrders(Long userId) {

        //Get all orders done by particular user
        List<OrderEntity> orders = orderRepository.findAllByUserEntityIdOrderByOrderDateDesc(userId);

        return orders
                .stream()
                .map(orderEntity -> modelMapper.map(orderEntity, OrderResponseDto.class))
                .toList();

    }

    public List<OrderItemResponseDto> getOrderDetails(Long orderId) {

        List<OrderItemsEntity> orderItems = orderItemsRepository.findAllByOrderEntityId(orderId);

        List<OrderItemResponseDto> orderItemResponseDtos = new ArrayList<>();
        for(OrderItemsEntity orderItem : orderItems){
            OrderItemResponseDto orderItemResponseDto = OrderItemResponseDto.builder()
                    .id(orderItem.getId())
                    .quantity(orderItem.getQuantity())
                    .price(orderItem.getPrice())
                    .productId(orderItem.getProductEntity().getId())
                    .productName(orderItem.getProductEntity().getName())
                    .build();
            orderItemResponseDtos.add(orderItemResponseDto);
        }
        return orderItemResponseDtos;
    }
}
