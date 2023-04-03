package com.project.anyahajo.service.impl;

import com.project.anyahajo.model.Item;
import com.project.anyahajo.repository.ItemRepository;
import com.project.anyahajo.service.ItemService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    @NonNull
    private ItemRepository itemRepository;



    @Override
    public void save(Item entity) {
        itemRepository.save(entity);
    }



    @Override
    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    @Override
    public Item findByItem_id(long parseLong) {
        return itemRepository.findByItem_id(parseLong);
    }

    @Override
    public Optional<Item> findById(Long id) {
        return itemRepository.findById(id);
    }

    @Override
    public List<Item> findByText(String text) {
        return itemRepository.findByText(text);
    }

    @Override
    public List<Item> findBySearch(String text, Class<?> forName) {
        return itemRepository.findBySearch(text,forName);
    }
}
