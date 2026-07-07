package com.task.e_commerce.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoginDto {

    @NotBlank
    @Email(message = "Please Enter valid email.")
    private String email;

    @NotBlank
    @Min(value = 8, message = "Password must contain minimum 8 character")
    @Pattern(regexp = "^(?=.{8,})(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$", message = "Password must contain One UpperCase Letter, One LowerCase Letter, One Special character from @,#,$,%,^,&,+,=")
    private String password;


}
