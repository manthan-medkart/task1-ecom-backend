package com.task.e_commerce.controllers;

import com.task.e_commerce.dtos.LoginDto;
import com.task.e_commerce.dtos.LoginResponseDto;
import com.task.e_commerce.dtos.SignupDto;
import com.task.e_commerce.dtos.SignupResponseDto;
import com.task.e_commerce.services.AuthService;
import com.task.e_commerce.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;


    public AuthController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping(path = "/signup")
    public ResponseEntity<SignupResponseDto> createNewUser(@RequestBody @Valid SignupDto user){

        SignupResponseDto savedUser = userService.createNewUser(user);

        savedUser.setStatus("Created");


        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }


    @PostMapping(path = "/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginDto loginDto){

        System.out.println("<-----Entry to Controller----->");
        LoginResponseDto loginResponseDto = authService.login(loginDto);

        System.out.println("<-----Exit to Controller----->");


        return new ResponseEntity<>(loginResponseDto, HttpStatus.OK);
    }


}
