package com.task.e_commerce.dtos;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignupDto {

    @NotBlank(message = "Please Enter your Name")
    private String name;

    @Email
    @NotBlank(message = "Please Enter your email")
    private String email;

    @NotBlank(message = "Please enter the password")
    @Min(value = 8, message = "Password must contain minimum 8 character")
    @Pattern(regexp = "^(?=.{8,})(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$", message = "Password must contain One UpperCase Letter, One LowerCase Letter, One Special character from @,#,$,%,^,&,+,=")
    private String password;



}
