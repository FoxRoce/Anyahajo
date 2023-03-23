package com.project.anyahajo.form;

import com.project.anyahajo.model.Availability;
import com.project.anyahajo.model.CarrierType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class ItemForm {

    @NotNull
    private String name;

    private Availability availability;
    private String description;
    private Byte[] picture;
    private Boolean isActive;

    private String babycareBrand = null;

    private String author = null;

    private CarrierType carrierType = null;
    private String carrierBrand = null;
    private String size = null;


}
