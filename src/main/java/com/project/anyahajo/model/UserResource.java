package com.project.anyahajo.model;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserResource {

    @Getter
    private List<User> users = new ArrayList<>();

    public User getUser(Long id){
        return users.stream()
                .filter(contact -> id.equals(contact.id()))
                .findAny()
                .orElse(null);
    }
}
