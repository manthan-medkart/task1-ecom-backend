package com.task.e_commerce.services;

import com.task.e_commerce.dtos.SignupDto;
import com.task.e_commerce.dtos.SignupResponseDto;
import com.task.e_commerce.entities.UserEntity;
import com.task.e_commerce.entities.enums.Roles;
import com.task.e_commerce.exceptions.ResourceNotFoundException;
import com.task.e_commerce.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(ModelMapper modelMapper, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow(() -> new ResourceNotFoundException("User with email : "+username +"not found" ));
    }

    public SignupResponseDto createNewUser(SignupDto user) {

        if(isUserExists(user.getEmail())) throw new ResourceNotFoundException("User already exists.");

        UserEntity toBeSavedEntity = modelMapper.map(user, UserEntity.class);
        toBeSavedEntity.setPassword(passwordEncoder.encode(user.getPassword()));
        toBeSavedEntity.setCreatedAt(LocalDateTime.now());
        toBeSavedEntity.setUpdatedAt(LocalDateTime.now());
        toBeSavedEntity.setRoles(Set.of(Roles.USER));

        UserEntity savedEntity = userRepository.save(toBeSavedEntity);

        return modelMapper.map(savedEntity, SignupResponseDto.class);

    }

    public UserEntity getUserById(Long id){
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("user with id : "+id+"does not exists."));

    }


    public Boolean isUserExists(String email){
        return userRepository.existsByEmail(email);
    }


}
