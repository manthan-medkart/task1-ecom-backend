package com.task.e_commerce.entities;

import com.task.e_commerce.entities.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private OrderStatus orderStatus;
    private Double totalPrice;
    private LocalDateTime orderDate;
    private String shippingAddress;
    private String paymentMethod;

    @ManyToOne
    private UserEntity userEntity;

    @OneToMany(mappedBy = "orderEntity")
    private List<OrderItemsEntity> orderItems;



}
