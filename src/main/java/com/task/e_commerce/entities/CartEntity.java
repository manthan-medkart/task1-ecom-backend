package com.task.e_commerce.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.BitcoinAddress;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "cart")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class CartEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne
    private UserEntity userEntity;

    @OneToMany(mappedBy = "cartEntity")
    private List<CartItemEntity> cartItemEntity;

}
