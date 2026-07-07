package com.task.e_commerce.services;

import com.task.e_commerce.dtos.ProductDto;
import com.task.e_commerce.entities.ProductEntity;
import com.task.e_commerce.exceptions.ResourceNotFoundException;
import com.task.e_commerce.repositories.ProductRepository;
import com.task.e_commerce.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {


    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public ProductService(ProductRepository productRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }


    public List<ProductDto> getAllProducts() {

        return productRepository.findAll()
                .stream()
                .map(productEntity -> modelMapper.map(productEntity, ProductDto.class))
                .toList();

    }

    public Optional<ProductDto> getMedicineById(Long id) {

        if(!isUserExists(id)) throw new ResourceNotFoundException("No medicine found with this id");

        Optional<ProductEntity> medicineEntity = productRepository.findById(id);
        return Optional.of(modelMapper.map(medicineEntity, ProductDto.class));

    }


    public Boolean isUserExists(Long id){
        return productRepository.existsById(id);
    }
}
