package com.project.anyahajo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
@Entity
@Table(name = "ah_item")
public class Item {
//    itemController - postMapping - saveItem

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long item_id;

    private Availability availability;
    private String name;
    private String description;
    private Byte[] picture;
    private boolean isActive = false;

}
