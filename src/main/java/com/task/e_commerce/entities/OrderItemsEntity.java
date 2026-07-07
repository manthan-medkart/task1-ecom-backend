package com.task.e_commerce.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class OrderItemsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private OrderEntity orderEntity;

    @OneToOne
    private ProductEntity productEntity;

}
