package com.project.anyahajo.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "ah_rent")
public class Rent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate startOfRent;
    private LocalDate endOfRent;


}
