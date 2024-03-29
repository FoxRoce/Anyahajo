package com.project.anyahajo.form;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetForm {

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{5,15}$", message = "A jelszónak legalább egy kis- és nagybetűt, valamint számot kell tartalmaznia.")
    private String password;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{5,15}$", message = "A jelszónak legalább egy kis- és nagybetűt, valamint számot kell tartalmaznia.")
    private String passwordCheck;
}
