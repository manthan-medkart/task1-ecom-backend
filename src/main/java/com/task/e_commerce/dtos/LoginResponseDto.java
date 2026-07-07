package com.task.e_commerce.dtos;

import com.task.e_commerce.dtos.components.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {

    private String accessToken;
    private UserDto userDto;

}
