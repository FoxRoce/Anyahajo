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

    private String name;
    private Availability availability;
    private LocalDate dateOfRent;
    private String description;
    private String shortDescription;
    private Byte[] picture;
    private boolean active = false;

//    @ManyToOne
//    @JoinColumn(name = "rent_id")
//    private Rent rent;

}
