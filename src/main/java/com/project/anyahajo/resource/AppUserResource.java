package com.project.anyahajo.resource;

import com.project.anyahajo.model.AppUser;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AppUserResource {

    @Getter
    private List<AppUser> users = new ArrayList<>();

    public AppUser getUser(Long id){
        return users.stream()
                .filter(user-> id.equals(user.getId()))
                .findAny()
                .orElse(null);
    }
}
