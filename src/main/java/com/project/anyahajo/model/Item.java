package com.project.anyahajo.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public abstract class Item {

    private Long id;
    private String name;
    private Category category;
    private boolean active = false;
    private LocalDate dateOfRent;

}
