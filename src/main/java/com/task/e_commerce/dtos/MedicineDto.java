package com.task.e_commerce.dtos;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicineDto {

    private Long id;
    private String name;
    private String composition;
    private Long mrp;
    private Long salesRate;
    private Long totalStrip;
    private Long medicinePerStrip;
    private String imageUrl;



}
