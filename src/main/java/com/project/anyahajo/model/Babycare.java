package com.project.anyahajo.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Babycare extends Item{

    private String brand;
}
