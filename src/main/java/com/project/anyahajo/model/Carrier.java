package com.project.anyahajo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Carrier extends Item {

    @Enumerated(EnumType.STRING)
    private CarrierType carrierType;
    private String carrierBrand;
    private String size;
}
