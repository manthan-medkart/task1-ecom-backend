package com.task.e_commerce.services;

import com.task.e_commerce.dtos.MedicineDto;
import com.task.e_commerce.entities.MedicineEntity;
import com.task.e_commerce.exceptions.ResourceNotFoundException;
import com.task.e_commerce.repositories.MedicineRepository;
import com.task.e_commerce.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedicineService {


    private final MedicineRepository medicineRepository;
    private final ModelMapper modelMapper;

    public MedicineService(MedicineRepository medicineRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.medicineRepository = medicineRepository;
        this.modelMapper = modelMapper;
    }


    public List<MedicineDto> getAllProducts() {

        return medicineRepository.findAll()
                .stream()
                .map(medicineEntity -> modelMapper.map(medicineEntity, MedicineDto.class))
                .toList();

    }

    public Optional<MedicineDto> getMedicineById(Long id) {

        if(!isUserExists(id)) throw new ResourceNotFoundException("No medicine found with this id");

        Optional<MedicineEntity> medicineEntity = medicineRepository.findById(id);
        return Optional.of(modelMapper.map(medicineEntity, MedicineDto.class));

    }


    public Boolean isUserExists(Long id){
        return medicineRepository.existsById(id);
    }
}
