package com.task.e_commerce.controllers;

import com.task.e_commerce.dtos.ProductDto;
import com.task.e_commerce.services.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/product")

public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllMedicine(){
        return new ResponseEntity<>(productService.getAllProducts(), HttpStatus.FOUND);
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<Optional<ProductDto>> getMedicineById(@PathVariable (name = "medId") Long id){
        System.out.println("_<----controller entered");
        return new ResponseEntity<>(productService.getMedicineById(id), HttpStatus.FOUND);
    }


}
