package com.project.anyahajo.form;

import com.project.anyahajo.model.Availability;
import com.project.anyahajo.model.CarrierType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;

@Getter
@Setter
public class ItemForm {

    @NotNull
    private String name;

    private Availability availability;
    private String description;
    private MultipartFile picture;
    private Boolean isActive;

    private String babycareBrand = null;

    private String author = null;

    private CarrierType carrierType = null;
    private String carrierBrand = null;
    private String size = null;

    public boolean pictureIsEmpty() throws IOException {
        return Arrays.toString(this.picture.getBytes()).isEmpty();
    }

}
