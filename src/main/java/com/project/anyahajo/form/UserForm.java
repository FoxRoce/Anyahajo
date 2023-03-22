package com.project.anyahajo.form;

import com.project.anyahajo.model.Name;
import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserForm {

    @Embedded
    private Long id;
    private Name name;
    private String email;
    private String phoneNumber;
    private boolean admin = false;
    private Boolean locked = false;
    private Boolean enabled = true;
}
