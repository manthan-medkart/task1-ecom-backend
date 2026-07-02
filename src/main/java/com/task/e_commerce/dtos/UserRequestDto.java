package com.task.e_commerce.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {

    private Long id;
    private String name;
    private String email;
    private String password;
    private Long phoneNo;
    private String gender;




}
