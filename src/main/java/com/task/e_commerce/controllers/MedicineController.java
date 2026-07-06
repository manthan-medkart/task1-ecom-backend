package com.task.e_commerce.controllers;

import com.task.e_commerce.dtos.MedicineDto;
import com.task.e_commerce.services.MedicineService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/product")

public class MedicineController {

    private final MedicineService medicineService;

    public MedicineController(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    @GetMapping(path = "/view")
    public ResponseEntity<List<MedicineDto>> getAllMedicine(){
        return new ResponseEntity<>(medicineService.getAllProducts(), HttpStatus.FOUND);
    }

    @GetMapping(path = "/authenticatedView/{medId}")
    public ResponseEntity<Optional<MedicineDto>> getMedicineById(@PathVariable (name = "medId") Long id){
        System.out.println("_<----controller entered");
        return new ResponseEntity<>(medicineService.getMedicineById(id), HttpStatus.FOUND);
    }


}
