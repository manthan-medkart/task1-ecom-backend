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

    public Optional<ProductDto> getProductById(Long productCode) {

        if (!isProductExists(productCode))
            throw new ResourceNotFoundException("No medicine found with this id");

        return Optional.of(modelMapper
                .map(productRepository
                        .findById(productCode)
                        , ProductDto.class
                ));

    }

    public Boolean isProductExists(Long productCode) {
        return productRepository.existsByProductCode(productCode);
    }

    public ProductDto publishProductById(Long productCode, ProductDto productDto) {
        if(!isProductExists(productCode)) {
            ProductEntity toBeSavedEntity = productRepository.findByProductCode(modelMapper.map(productDto, ProductEntity.class));
            return modelMapper.map(productRepository.save(toBeSavedEntity), ProductDto.class);
        }
        else{
            return modelMapper.map(productRepository.save(modelMapper.map(productDto, ProductEntity.class)), ProductDto.class);
        }
    }
}
