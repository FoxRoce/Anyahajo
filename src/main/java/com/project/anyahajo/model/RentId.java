package com.project.anyahajo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class RentId implements Serializable {

    @Column(name = "item_id")
    private Long item_id;

    @Column(name = "user_id")
    private Long user_id;
}
