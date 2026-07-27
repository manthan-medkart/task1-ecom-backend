package com.task.e_commerce.services;

import com.task.e_commerce.dtos.ProductDto;
import com.task.e_commerce.dtos.ProductPublishDto;
import com.task.e_commerce.entities.ProductEntity;
import com.task.e_commerce.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private ProductService productService;

    private ProductPublishDto productPublishDto;
    private ProductEntity productEntity;

    @BeforeEach
    void setUp() {
        productPublishDto = new ProductPublishDto();
        productPublishDto.setProductCode(12345L);
        productPublishDto.setName("Test Product");
        productPublishDto.setComposition("Test Composition");

        productEntity = new ProductEntity();
        productEntity.setId(1L);
        productEntity.setProductCode(12345L);
        productEntity.setName("Test Product");
        productEntity.setComposition("Test Composition");
    }

    @Test
    void publishProductById_WhenProductDoesNotExist_ShouldSaveAsNew() {
        // Arrange
        Long productCode = 12345L;
        when(productRepository.existsByProductCode(productCode)).thenReturn(false);
        when(productRepository.save(any(ProductEntity.class))).thenAnswer(invocation -> {
            ProductEntity entity = invocation.getArgument(0);
            entity.setId(1L); // simulate auto-generation of ID
            return entity;
        });

        // Act
        ProductDto result = productService.publishProductByProductCode(productCode, productPublishDto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(productCode, result.getProductCode());
        verify(productRepository, times(1)).existsByProductCode(productCode);
        verify(productRepository, never()).findByProductCode(anyLong());
        verify(productRepository, times(1)).save(any(ProductEntity.class));
    }

    @Test
    void publishProductById_WhenProductExists_ShouldUpdateExisting() {
        // Arrange
        Long productCode = 12345L;
        when(productRepository.existsByProductCode(productCode)).thenReturn(true);
        when(productRepository.findByProductCode(productCode)).thenReturn(productEntity);
        when(productRepository.save(any(ProductEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ProductDto result = productService.publishProductByProductCode(productCode, productPublishDto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(productCode, result.getProductCode());
        verify(productRepository, times(1)).existsByProductCode(productCode);
        verify(productRepository, times(1)).findByProductCode(productCode);
        verify(productRepository, times(1)).save(any(ProductEntity.class));
    }
}
