package com.project.anyahajo.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Carrier extends Item {

    private CarrierType carrierType;
    private String carrierBrand;
    private String size;
}
