package com.project.anyahajo.resource;

import com.project.anyahajo.model.User;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AppUserResource {

    @Getter
    private List<User> users = new ArrayList<>();

    public User getUser(Long id){
        return users.stream()
                .filter(user-> id.equals(user.getId()))
                .findAny()
                .orElse(null);
    }
}
