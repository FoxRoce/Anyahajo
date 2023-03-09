package com.project.anyahajo.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Carrier extends Item {

    private CarrierType type;
    private String brand;
    private String size;
}
