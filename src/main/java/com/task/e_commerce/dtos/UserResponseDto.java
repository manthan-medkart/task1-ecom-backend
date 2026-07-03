package com.task.e_commerce.dtos;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDto {

    private String email;
    private String status;
    private Long totalprice;

}
