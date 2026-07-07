package com.task.e_commerce.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "cart")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class CartEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToOne
    private UserEntity userEntity;

    @OneToMany(mappedBy = "cartEntity")
    private List<CartItemEntity> cartItemEntity;

}
