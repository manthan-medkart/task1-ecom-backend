package com.task.e_commerce.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long quantity;
    private Double price;

    @ManyToOne
    private ProductEntity productEntity;

    @ManyToOne
    private OrderEntity orderEntity;

}
