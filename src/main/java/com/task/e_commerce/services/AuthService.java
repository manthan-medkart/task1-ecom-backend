package com.task.e_commerce.services;

import com.task.e_commerce.dtos.LoginDto;
import com.task.e_commerce.dtos.LoginResponseDto;
import com.task.e_commerce.dtos.components.UserDto;
import com.task.e_commerce.entities.UserEntity;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final ModelMapper modelMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService, ModelMapper modelMapper){
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.modelMapper = modelMapper;
    }

    public LoginResponseDto login(LoginDto loginDto){
        System.out.println("1. Authentication Service entered......");
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );

        System.out.println("2. Successfully Authenticated.");

        UserEntity user = (UserEntity) authentication.getPrincipal();

        System.out.println("3. Get Authenticated User from db......");

        String accessToken = jwtService.generateAccessToken(user);

        System.out.println("4. JWT token generated");

        return new LoginResponseDto(accessToken, modelMapper.map(user, UserDto.class));



    }

}
