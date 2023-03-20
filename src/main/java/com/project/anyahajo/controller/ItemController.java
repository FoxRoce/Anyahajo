package com.project.anyahajo.controller;

import com.project.anyahajo.form.ItemForm;
import com.project.anyahajo.model.*;
import com.project.anyahajo.repository.AppUserRepository;
import com.project.anyahajo.repository.ItemRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ItemController {

    @NonNull
    private ItemRepository itemRepository;
    private AppUserRepository appUserRepository;

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
        if (bindingResult.hasErrors()) {
            return "admin-add-item";
        }
        Item entity;

        if (itemForm.getBabycareBrand() != null) {
            entity = new Babycare();
            ((Babycare) entity).setBabycareBrand(itemForm.getBabycareBrand());
        } else if (itemForm.getAuthor() != null) {
            entity = new Book();
            ((Book) entity).setAuthor(itemForm.getAuthor());
        } else if (itemForm.getCarrierBrand() != null) {
            entity = new Carrier();
            ((Carrier) entity).setCarrierBrand(itemForm.getCarrierBrand());
            ((Carrier) entity).setType(itemForm.getType());
            ((Carrier) entity).setSize(itemForm.getSize());
        } else {
            entity = new Item();
        }

        entity.setName(itemForm.getName());
        entity.setAvailability(itemForm.getAvailability());
        entity.setDescription(itemForm.getDescription());
        entity.setPicture(itemForm.getPicture());
        entity.setActive(itemForm.isActive());

//        itemRepository.insertInto

        return "redirect:/kolcsonzes";
    }
    @GetMapping(path = "/{userId}/kosar")
    public String showBasket(@PathVariable("userId") Long userId, Model model) {
        model.addAttribute("user", appUserRepository.findById(userId));
        return "basket";
    }



}
