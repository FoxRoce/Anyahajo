package com.project.anyahajo.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationForm {

//    @NotBlank
//    private String name;

    private String lastName;
    private String firstName;
    @Email
    private String email;

    @Size(min = 5, max = 15)
    private String password;

    @Size(min = 5, max = 15)
    private String passwordCheck;

}
