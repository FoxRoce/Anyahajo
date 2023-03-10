package com.project.anyahajo.form;

import com.project.anyahajo.model.Availability;
import com.project.anyahajo.model.CarrierType;
import jakarta.validation.constraints.Future;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class ItemForm {

    private String name;

    @Future
    @DateTimeFormat(pattern = "YYYY-MM-DD")
    private LocalDate dateOfRent;

    private Availability availability;
    private String description;
    private Byte[] picture;
    private boolean isActive;

    private String babycareBrand = null;

    private String author = null;

    private CarrierType type = null;
    private String carrierBrand = null;
    private String size = null;


}
