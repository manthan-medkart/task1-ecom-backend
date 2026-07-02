package com.task.e_commerce.controllers;

import com.task.e_commerce.dtos.UserRequestDto;
import com.task.e_commerce.dtos.UserResponseDto;
import com.task.e_commerce.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/registration")
public class UserController {

    public UserController(UserService userService) {
        this.userService = userService;
    }

    private final UserService userService;

    @PostMapping(path = "/create")
    public ResponseEntity<UserResponseDto> createNewUser(@RequestBody UserRequestDto user){

        UserResponseDto savedUser = userService.createNewUser(user);

        savedUser.setStatus("Created");

        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

}
