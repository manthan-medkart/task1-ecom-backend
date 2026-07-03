package com.task.e_commerce.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.)
    private Long id;
    private String name;
    private String email;
    private String password;
    private Long phoneNo;
    private String gender;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
