package com.task.e_commerce.controllers;

import com.task.e_commerce.dtos.LoginDto;
import com.task.e_commerce.dtos.LoginResponseDto;
import com.task.e_commerce.dtos.UserRequestDto;
import com.task.e_commerce.dtos.UserResponseDto;
import com.task.e_commerce.services.AuthService;
import com.task.e_commerce.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/registration")
public class UserController {

    private final AuthService authService;
    private final UserService userService;


    public UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping(path = "/signUp")
    public ResponseEntity<UserResponseDto> createNewUser(@RequestBody UserRequestDto user){

        UserResponseDto savedUser = userService.createNewUser(user);

        savedUser.setStatus("Created");


        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }


    @PostMapping(path = "/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginDto loginDto){

        System.out.println("<-----Entry to Controller----->");
        LoginResponseDto loginResponseDto = authService.login(loginDto);

        System.out.println("<-----Exit to Controller----->");


        return new ResponseEntity<>(loginResponseDto, HttpStatus.OK);
    }


}
