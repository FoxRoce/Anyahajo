package com.project.anyahajo.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "ah_rent")
public class Rent {

    @EmbeddedId
    private RentId rent_id = new RentId();
    @ManyToOne
    private Item item;
    @ManyToOne
    private User user;

    private LocalDate startOfRent;
    private LocalDate endOfRent;


}
