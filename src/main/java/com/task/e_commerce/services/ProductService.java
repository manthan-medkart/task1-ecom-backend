package com.task.e_commerce.services;

import com.task.e_commerce.dtos.ProductDto;
import com.task.e_commerce.entities.ProductEntity;
import com.task.e_commerce.exceptions.ResourceNotFoundException;
import com.task.e_commerce.repositories.ProductRepository;
import com.task.e_commerce.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public Page<ProductDto> getProducts(String search, String sort, int page, int size) {
        Sort sortingRule = Sort.unsorted();
        if ("PRICE_LOW_HIGH".equalsIgnoreCase(sort)) {
            sortingRule = Sort.by(Sort.Direction.ASC, "salesRate");
        } else if ("PRICE_HIGH_LOW".equalsIgnoreCase(sort)) {
            sortingRule = Sort.by(Sort.Direction.DESC, "salesRate");
        }

        Pageable pageable = PageRequest.of(page, size, sortingRule);
        Page<ProductEntity> productsPage = productRepository.searchProducts(search, pageable);

        return productsPage.map(productEntity -> modelMapper.map(productEntity, ProductDto.class));
    }

    public Optional<ProductDto> getMedicineById(Long id) {

        if (!isUserExists(id))
            throw new ResourceNotFoundException("No medicine found with this id");

        Optional<ProductEntity> medicineEntity = productRepository.findById(id);
        return Optional.of(modelMapper.map(medicineEntity, ProductDto.class));

    }

    public Boolean isUserExists(Long id) {
        return productRepository.existsById(id);
    }

    public ProductDto publishProductById(Long id, ProductDto productDto) {

    }
}
