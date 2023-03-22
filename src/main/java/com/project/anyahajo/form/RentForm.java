package com.project.anyahajo.form;

import com.project.anyahajo.model.Item;
import com.project.anyahajo.model.User;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class RentForm {

    private Long item_id;
    private Long user_id;

    private LocalDate startOfRent;
    private LocalDate endOfRent;
    private boolean isExtended;
    private int price;
    private int deposit;
    private int payBackAmount;
}
