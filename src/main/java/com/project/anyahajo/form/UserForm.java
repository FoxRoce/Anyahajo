package com.project.anyahajo.form;

import com.project.anyahajo.model.Name;
import com.project.anyahajo.model.Role;
import jakarta.persistence.Embedded;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserForm {

    @Embedded
    private Long id;
    private Name name;
    @NotBlank
    @Email
    private String email;
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{5,15}$", message = "A jelszónak legalább egy kis- és nagybetűt, valamint számot kell tartalmaznia.")
    private String password;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{5,15}$", message = "A jelszónak legalább egy kis- és nagybetűt, valamint számot kell tartalmaznia.")
    private String passwordCheck;

    private String phoneNumber;
    private Role role;
    private Boolean locked = false;
    private Boolean enabled = true;
    private Set<Long> basket = new LinkedHashSet<>();
    private String resetPasswordToken;
    private String enableUrl;

}
