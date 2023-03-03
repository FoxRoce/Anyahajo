package com.project.anyahajo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
@Entity
public class Item {
//    ez kell legyen az ososzaly, szoval a targyfelvetel nem jo
//    itemController - postMapping - saveItem

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Category category;
    private boolean active = false;
    private LocalDate dateOfRent;

}
