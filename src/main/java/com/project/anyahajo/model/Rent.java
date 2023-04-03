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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rent_id;
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDate startOfRent;
    private LocalDate endOfRent;
    private boolean isExtended;
    private Integer price;
    private Integer deposit;
    private Integer payBackAmount;

    private LocalDate history;
}
