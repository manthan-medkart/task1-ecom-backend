package com.task.e_commerce.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "product")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "product_code", unique = true, nullable = false)
    private Long productCode;
    private String name;
    private String composition;
    private Long mrp;
    private Long salesRate;
    private Long totalStrip;
    private Long medicinePerStrip;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;



}
