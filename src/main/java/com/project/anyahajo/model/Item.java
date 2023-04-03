package com.project.anyahajo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

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

    @Enumerated(EnumType.STRING)
    private Availability availability;
    private String name;
    private String description;

    @Lob
    @Column(length=1000000)
    private byte[] picture;

    private boolean isActive = false;
}
