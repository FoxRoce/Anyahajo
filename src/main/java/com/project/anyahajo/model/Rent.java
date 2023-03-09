package com.project.anyahajo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "ah_rent")
public class Rent {

    @EmbeddedId
    private RentId rent_id = new RentId();
    @ManyToOne
    @MapsId("item_id")
    private Item item;
    @ManyToOne
    @MapsId("user_id")
    private User user;

    private LocalDate startOfRent;
    private LocalDate endOfRent;
    private boolean isExtended;
    private int price;
    private int deposit;
    private int payBackAmount;
}
