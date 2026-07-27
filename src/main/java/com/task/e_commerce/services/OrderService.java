package com.task.e_commerce.services;

import com.task.e_commerce.dtos.OrderItemResponseDto;
import com.task.e_commerce.dtos.OrderRequestDto;
import com.task.e_commerce.dtos.OrderResponseDto;
import com.task.e_commerce.entities.*;
import com.task.e_commerce.entities.enums.OrderStatus;
import com.task.e_commerce.exceptions.ResourceNotFoundException;
import com.task.e_commerce.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private final RestClient restClient;

    public OrderService(OrderRepository orderRepository,
                        OrderItemsRepository orderItemsRepository,
                        CartRepository cartRepository,
                        CartItemRepository cartItemRepository,
                        UserRepository userRepository,
                        ProductRepository productRepository, ModelMapper modelMapper, RestClient restClient) {
        this.orderRepository = orderRepository;
        this.orderItemsRepository = orderItemsRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
        this.restClient = restClient;
    }

    @Transactional
    public OrderResponseDto placeOrder(Long userId, String email, OrderRequestDto orderRequestDto) {
        //get active cart of the user id passed
        CartEntity cartEntity = cartRepository.findByUserEntityIdAndActive(userId, true);
        System.out.println("1. Active Cart Found.");

        //if found no active cart ---> means no cart Items in cart
        if (cartEntity == null) {
            throw new IllegalArgumentException("Cannot place order: No active cart found.");
        }

        //Fetch all Items related to that particular cartId
        List<CartItemEntity> cartItems = cartItemRepository.findAllByCartEntityId(cartEntity.getId());
        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cannot place order: Cart is empty.");
        }
        System.out.println("2. Cart items found.");

        // Check stock availability from WMS for each item before placing order
        for (CartItemEntity cartItem : cartItems) {
            ProductEntity product = cartItem.getProductEntity();

            // Fetch stock from WMS (single source of truth)
            int wmsStock = fetchStockFromWms(product.getProductCode());

            // If stock is not available ---> throw exception and message
            if (cartItem.getQuantity() > wmsStock) {
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getName()
                        + ". Available: " + wmsStock + ", Requested: " + cartItem.getQuantity());
            }
        }

        System.out.println("3. All items is available.");

        double totalPrice = cartItems.stream()
                .mapToDouble(cartItem -> cartItem.getProductEntity().getSalesRate() * cartItem.getQuantity())
                .sum();

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id: " + userId + " does not exist."));
        System.out.println("4. User found successfully.");

        OrderEntity orderEntity = OrderEntity.builder()
                .userEntity(user)
                .orderStatus(OrderStatus.PENDING)
                .totalPrice(totalPrice)
                .orderDate(LocalDateTime.now())
                .shippingAddress(orderRequestDto.getShippingAddress())
                .paymentMethod(orderRequestDto.getPaymentMethod())
                .build();

        orderRepository.save(orderEntity);
        System.out.println("5. Order saved.");

        //Enter all the items that has been ordered into OrderItems
        List<OrderItemsEntity> savedOrderItems = new ArrayList<>();
        for (CartItemEntity cartItem : cartItems) {
            ProductEntity product = cartItem.getProductEntity();
            OrderItemsEntity orderItem = OrderItemsEntity.builder()
                    .quantity(cartItem.getQuantity())
                    .price(cartItem.getPrice())
                    .productEntity(product)
                    .orderEntity(orderEntity)
                    .build();

            orderItemsRepository.save(orderItem);
            savedOrderItems.add(orderItem);
            System.out.println("5.1 orderItem Saved");
            // Deduct stock from WMS (single source of truth) + creates stock log
            deductStockFromWms(email, product.getProductCode(), cartItem.getQuantity());
            System.out.println("5.2 stock deducted");
        }
        System.out.println("6. Cart Items Inserted to Order Items.");
        orderEntity.setOrderItems(savedOrderItems);

        //Deactivate the cart
        cartEntity.setActive(false);
        cartEntity.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cartEntity);

        System.out.println("7. Cart deactivated.");

        // Prepare WMS payload to create sales order
        Map<String, Object> wmsPayload = Map.of(
            "ecommerce_order_id", orderEntity.getId().toString(),
            "customer_name", user.getName(),
            "customer_email", user.getEmail(),
            "items", cartItems.stream().map(item -> Map.of(
                "product_code", item.getProductEntity().getProductCode(),
                "quantity", item.getQuantity()
            )).toList()
        );

        System.out.println("8. Payload Created");
        System.out.println(wmsPayload);

        try {
            restClient.post()
                    .uri("/api/sales-orders/create")
                    .body(wmsPayload)
                    .retrieve()
                    .toBodilessEntity();

            System.out.println("9. Successfully api response");

        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            throw new RuntimeException("Failed to register order in WMS: " + e.getMessage(), e);
        }

        return mapToOrderResponseDto(orderEntity);

    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<OrderResponseDto> getUserOrders(Long userId) {

        //Get all orders done by particular user
        List<OrderEntity> orders = orderRepository.findAllByUserEntityIdOrderByOrderDateDesc(userId);

        return orders
                .stream()
                .map(this::mapToOrderResponseDto)
                .toList();

    }

    @Transactional
    public OrderResponseDto updateOrderStatus(Long orderId, OrderStatus status) {
        System.out.println("Service : Change Status");
        OrderEntity orderEntity = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order with id: " + orderId + " does not exist."));
        orderEntity.setOrderStatus(status);
        orderEntity = orderRepository.save(orderEntity);
        System.out.println("status change to : "+orderEntity.getOrderStatus());
        return mapToOrderResponseDto(orderEntity);

    }


    private OrderResponseDto mapToOrderResponseDto(OrderEntity orderEntity) {

        List<OrderItemResponseDto> items = new ArrayList<>();
        if (orderEntity.getOrderItems() != null) {
            for (OrderItemsEntity item : orderEntity.getOrderItems()) {
                items.add(OrderItemResponseDto.builder()
                        .id(item.getId())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .productId(item.getProductEntity().getId())
                        .productName(item.getProductEntity().getName())
                        .build());
            }
        }

        OrderResponseDto dto = OrderResponseDto.builder()
                .id(orderEntity.getId())
                .orderStatus(orderEntity.getOrderStatus())
                .totalPrice(orderEntity.getTotalPrice())
                .orderDate(orderEntity.getOrderDate())
                .shippingAddress(orderEntity.getShippingAddress())

                .paymentMethod(orderEntity.getPaymentMethod())
                .items(items)
                .build();
        dto.setId(orderEntity.getId());

        System.out.println("-> Dto converted.");
        return dto;
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

    /**
     * Fetch current stock quantity from WMS for a given product.
     * WMS is the single source of truth for stock.
     *
     * @param productCode the product code
     * @return the current stock quantity
     */
    private int fetchStockFromWms(Long productCode) {
        try {
            System.out.println("<---Entered Fetching of Stock--->");
            Map response = restClient.get()
                    .uri("/api/stock/product/" + productCode)
                    .retrieve()
                    .body(Map.class);
            System.out.println(response.get("data"));
            if (response != null && response.containsKey("data")) {
                Map data = (Map) response.get("data");
                if (data != null && data.containsKey("quantity")) {
                    return ((Number) data.get("quantity")).intValue();
                }
            }
            return 0;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch stock from WMS for product: " + productCode
                    + ". Error: " + e.getMessage(), e);
        }
    }

    /**
     * Deduct stock from WMS for a given product.
     * This also creates a stock log in WMS.
     *
     * @param productCode the product code
     * @param quantity the quantity to deduct
     */
    private void deductStockFromWms(String email, Long productCode, Long quantity) {
        try {
            Map<String, Object> payload = Map.of(
                "product_code", productCode,
                    "quantity", quantity
            );

            restClient.patch()
                    .uri("/api/stock/product/deduct")
                    .headers(httpHeaders -> {
                        httpHeaders.add("email", email);
                        httpHeaders.add("source", "ECOMMERCE");
                        httpHeaders.add("movement_type", "PURCHASE");
                    })
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            throw new RuntimeException("Failed to deduct stock in WMS for product: " + productCode
                    + ". Error: " + e.getMessage(), e);
        }
    }

    public Boolean isOrderExists(Long orderId) {
        try{
            return orderRepository.existsById(orderId);
        }catch (Exception exception){
            throw new RuntimeException("Failed to fetch order existence.    Error : "+exception.getMessage());
        }

    }
}
