package com.task.e_commerce.services;

import com.task.e_commerce.dtos.UserRequestDto;
import com.task.e_commerce.dtos.UserResponseDto;
import com.task.e_commerce.entities.UserEntity;
import com.task.e_commerce.exceptions.ResourceNotFoundException;
import com.task.e_commerce.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public UserService(ModelMapper modelMapper, UserRepository userRepository) {
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
    }

    public UserResponseDto createNewUser(UserRequestDto user) {

        if(isUserExists(user.getEmail())) throw new ResourceNotFoundException("User already exists.");

        UserEntity toBeSavedEntity = modelMapper.map(user, UserEntity.class);
        toBeSavedEntity.setCreatedAt(LocalDateTime.now());
        toBeSavedEntity.setUpdatedAt(LocalDateTime.now());

        UserEntity savedEntity = userRepository.save(toBeSavedEntity);

        return modelMapper.map(savedEntity, UserResponseDto.class);

    }


    public Boolean isUserExists(String email){
        return userRepository.existsByEmail(email);
    }
}
