package com.task.e_commerce.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "medicine")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicineEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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
