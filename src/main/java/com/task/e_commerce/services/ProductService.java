package com.task.e_commerce.services;

import com.task.e_commerce.dtos.ProductDto;
import com.task.e_commerce.dtos.ProductPublishDto;
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

import java.time.LocalDateTime;
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

    public ProductDto publishProductById(Long productCode, ProductPublishDto productPublishDto) {
        productPublishDto.setProductCode(productCode);
        if (!isProductExists(productCode)) {
            System.out.println("----0----");
            ProductEntity entity = ProductEntity.builder()
                    .name(productPublishDto.getName())
                    .productCode(productPublishDto.getProductCode())
                    .composition(productPublishDto.getComposition())
                    .mrp(productPublishDto.getMrp())
                    .salesRate(productPublishDto.getSales_rate())
                    .totalStrip(productPublishDto.getTotal_strip())
                    .medicinePerStrip(productPublishDto.getMedicine_per_strip())
                    .imageUrl(productPublishDto.getImage_url())
                    .build();
            entity.setCreatedAt(LocalDateTime.now());
            entity.setUpdatedAt(LocalDateTime.now());
            System.out.println("------1-------");
            return modelMapper.map(productRepository.save(entity), ProductDto.class);
        } else {
            System.out.println("------2-----");
            ProductEntity existingEntity = productRepository.findByProductCode(productCode);
            System.out.println(existingEntity);
            System.out.println("------3-----");
            ProductEntity entityToUpdate = ProductEntity.builder()
                    .name(productPublishDto.getName())
                    .productCode(existingEntity.getProductCode())
                    .composition(productPublishDto.getComposition())
                    .mrp(productPublishDto.getMrp())
                    .salesRate(productPublishDto.getSales_rate())
                    .totalStrip(productPublishDto.getTotal_strip())
                    .medicinePerStrip(productPublishDto.getMedicine_per_strip())
                    .imageUrl(productPublishDto.getImage_url())
                    .build();
            entityToUpdate.setId(existingEntity.getId());
            if (existingEntity.getCreatedAt() != null) {
                System.out.println("----4----");
                entityToUpdate.setCreatedAt(existingEntity.getCreatedAt());
            } else {
                System.out.println("----5----");
                entityToUpdate.setCreatedAt(LocalDateTime.now());
            }
            System.out.println("----6----");
            entityToUpdate.setUpdatedAt(LocalDateTime.now());
            System.out.println("----7----");
            System.out.println(entityToUpdate);
            ProductEntity savedEntity = productRepository.save(entityToUpdate);
            System.out.println("----8----");
            return modelMapper.map(savedEntity, ProductDto.class);
        }
    }
}
