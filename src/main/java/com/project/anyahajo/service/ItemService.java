package com.project.anyahajo.service;

import com.project.anyahajo.model.Availability;
import com.project.anyahajo.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemService {

    void save(Item entity);


    List<Item> findAll();

    Item findByItem_id(long parseLong);

    Optional<Item> findById(Long id);

    List<Item> findByText(String text);

    List<Item> findBySearch(String text, Class<?> forName);

    List<Item> findRentable(Availability available);
}
