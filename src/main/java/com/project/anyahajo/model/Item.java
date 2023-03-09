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
//    ez kell legyen az ososzaly, szoval a targyfelvetel nem jo
//    itemController - postMapping - saveItem

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Availability availability;
    private String name;
    private Byte[] picture;
    private boolean active = false;

    private LocalDate dateOfRent;

}
