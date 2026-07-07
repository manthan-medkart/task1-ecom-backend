package com.task.e_commerce.dtos.components;

import com.task.e_commerce.entities.enums.Roles;
import lombok.Data;

@Data
public class UserDto {

    private Long id;
    private String name;
    private String email;
    private Roles roles;

}
