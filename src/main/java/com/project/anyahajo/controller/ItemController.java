package com.project.anyahajo.controller;

import com.project.anyahajo.form.ItemForm;
import com.project.anyahajo.model.*;
import com.project.anyahajo.repository.ItemRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {

    @NonNull
    private ItemRepository itemRepository;

    @GetMapping(path = {"/kolcsonzes"})
    public String listItems(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "all-items";
    }
    @GetMapping(path = {"/books"})
    public String listItems() {
        return "redirect:/kolcsonzes?itemType=Book";
    }

    @GetMapping("/kolcsonzes/kereses")
    public String searchItems(Model model, @RequestParam(value = "text") String text) {
        List<Item> items = itemRepository.findByText(text);
        model.addAttribute("items", items);
        return "all-items";
    }

    @GetMapping(path = {"/admin/ujTargyFelvetel"})
    public String newItem(Model model) {
//        System.out.println("Inside Get mapping");
        model.addAttribute("newItem", new ItemForm());
        return "admin-add-item";
    }

    @PostMapping(path = {"/admin/ujTargyFelvetel"})
    public String saveItem(
            @ModelAttribute("newItem")
            @Validated
            ItemForm itemForm,
            BindingResult bindingResult
    ) {
//        System.out.println("Inside post mapping");
        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult);
            return "admin-add-item";
        }

        Item entity;

        if (!itemForm.getBabycareBrand().isEmpty()) {
            entity = new Babycare();
            ((Babycare) entity).setBabycareBrand(itemForm.getBabycareBrand());
        } else if (!itemForm.getAuthor().isEmpty()) {
            entity = new Book();
            ((Book) entity).setAuthor(itemForm.getAuthor());
        } else if (!itemForm.getCarrierBrand().isEmpty()) {
            entity = new Carrier();
            ((Carrier) entity).setCarrierBrand(itemForm.getCarrierBrand());
            ((Carrier) entity).setCarrierType(itemForm.getCarrierType());
            ((Carrier) entity).setSize(itemForm.getSize());
        } else {
            entity = new Item();
        }

        entity.setName(itemForm.getName());
        entity.setAvailability(itemForm.getAvailability());
        entity.setDescription(itemForm.getDescription());
        entity.setPicture(itemForm.getPicture());
        entity.setActive(itemForm.getIsActive());

        itemRepository.save(entity);

        return "redirect:/kolcsonzes";
    }


    @GetMapping("/item/{id}")
    public String getItem(@PathVariable("id") String id, Model model) {
        List<Item> items = itemRepository.findAll();
        for(Item i : items){
            if(i.getItem_id() == Integer.parseInt(id)){
                model.addAttribute("item", i);
                return "item";
            }
        }
        return "redirect:/kolcsonzes";
    }


}
