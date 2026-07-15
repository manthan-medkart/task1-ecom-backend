package com.task.e_commerce.controllers;

import com.task.e_commerce.dtos.ProductDto;
import com.task.e_commerce.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<Page<ProductDto>> getAllMedicine(
            @RequestParam(value = "search", required = false, defaultValue = "") String search,
            @RequestParam(value = "sort", required = false, defaultValue = "DEFAULT") String sort,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "6") int size) {
        return new ResponseEntity<>(productService.getProducts(search, sort, page, size), HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Optional<ProductDto>> getMedicineById(@PathVariable Long id) {
        System.out.println("_<----controller entered");
        return new ResponseEntity<>(productService.getMedicineById(id), HttpStatus.OK);
    }

//    @PostMapping(path = "/publish/{id}")
//    public ResponseEntity   <ProductDto> publishProductById(@RequestBody ProductDto productDto, @PathVariable Long id){
//        return new ResponseEntity<>(productService.publishProductById(id, productDto));
//    }
}
