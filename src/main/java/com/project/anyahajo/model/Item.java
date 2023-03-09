package com.project.anyahajo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Item {
//    itemController - postMapping - saveItem

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Availability availability;
    private String name;
    private String description;
    private Byte[] picture;
    private boolean isActive = false;
    private LocalDate dateOfRent;

//    @ManyToOne
//    @JoinColumn(name = "rent_id")
//    private Rent rent;
}
