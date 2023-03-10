package com.project.anyahajo.resource;

import com.project.anyahajo.model.Item;
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
                .filter(item -> id.equals(item.getItem_id()))
                .findAny()
                .orElse(null);
    }

}
