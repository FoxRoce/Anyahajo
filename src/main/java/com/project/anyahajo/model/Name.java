package com.project.anyahajo.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Name {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;

    @Override
    public String toString() {
        return lastName + " " + firstName;
    }
}
