package com.project.anyahajo.model;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ItemResource {

    @Getter
    private List<Item> items = new ArrayList<>();

    public Item getItem(Long id){
        return items.stream()
                .filter(contact -> id.equals(contact.id()))
                .findAny()
                .orElse(null);
    }

}
