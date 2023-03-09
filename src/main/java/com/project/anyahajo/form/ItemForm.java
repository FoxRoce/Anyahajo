package com.project.anyahajo.form;

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
}
