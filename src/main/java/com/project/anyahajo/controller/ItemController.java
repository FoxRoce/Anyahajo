package com.project.anyahajo.controller;

import com.project.anyahajo.form.ItemForm;
import com.project.anyahajo.model.*;
import com.project.anyahajo.repository.BabycareRepository;
import com.project.anyahajo.repository.BookRepository;
import com.project.anyahajo.repository.CarrierRepository;
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

@Controller
@RequiredArgsConstructor
public class ItemController {

    @NonNull
    private ItemRepository itemRepository;
    @NonNull
    private BookRepository bookRepository;
    @NonNull
    private CarrierRepository carrierRepository;
    @NonNull
    private BabycareRepository babycareRepository;


    @GetMapping(path = {"/kolcsonzes"})
    public String listItems(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "all-items";
    }

    @GetMapping(path = {"/kolcsonzes/kony"})
    public String listBooks(Model model) {
        List<Book> books = bookRepository.findAll();
        model.addAttribute("books", books);
        return "all-books";
    }

    @GetMapping(path = {"/kolcsonzes/hordozo"})
    public String listCarriers(Model model) {
        List<Carrier> carriers = carrierRepository.findAll();
        model.addAttribute("carriers", carriers);
        return "all-carriers";
    }

    @GetMapping(path = {"/kolcsonzes/babaapolas"})
    public String listBabycares(Model model) {
        List<Babycare> babycares = babycareRepository.findAll();
        model.addAttribute("babycares", babycares);
        return "all-babycares";
    }

    @GetMapping("/kolcsonzes/kereses")
    public String searchItems(Model model, @RequestParam(value = "text") String text) {
        List<Item> items = itemRepository.findByText(text);
        model.addAttribute("items", items);
        return "all-items";
    }

    @GetMapping(path = {"/admin/ujTargyFelvetel"})
    public String newItem(Model model) {
        System.out.println("Inside Get mapping");
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
        System.out.println("Inside post mapping");
        if (bindingResult.hasErrors()) {
            return "admin-add-item";
        }
//        Item entity;

        if (itemForm.getBabycareBrand() != null) {
            Babycare entity = new Babycare();
            entity.setBabycareBrand(itemForm.getBabycareBrand());

            entity.setName(itemForm.getName());
            entity.setAvailability(itemForm.getAvailability());
            entity.setDescription(itemForm.getDescription());
            entity.setPicture(itemForm.getPicture());
            entity.setActive(itemForm.isActive());

            itemRepository.save(entity);

        } else if (itemForm.getAuthor() != null) {
            Book entity = new Book();
            entity.setAuthor(itemForm.getAuthor());

            entity.setName(itemForm.getName());
            entity.setAvailability(itemForm.getAvailability());
            entity.setDescription(itemForm.getDescription());
            entity.setPicture(itemForm.getPicture());
            entity.setActive(itemForm.isActive());

            itemRepository.save(entity);

        } else if (itemForm.getType() != null) {
            Carrier entity = new Carrier();
            entity.setCarrierBrand(itemForm.getCarrierBrand());
            entity.setType(itemForm.getType());
            entity.setSize(itemForm.getSize());

            entity.setName(itemForm.getName());
            entity.setAvailability(itemForm.getAvailability());
            entity.setDescription(itemForm.getDescription());
            entity.setPicture(itemForm.getPicture());
            entity.setActive(itemForm.isActive());

            itemRepository.save(entity);

        } else {
            Item entity = new Item();

            entity.setName(itemForm.getName());
            entity.setAvailability(itemForm.getAvailability());
            entity.setDescription(itemForm.getDescription());
            entity.setPicture(itemForm.getPicture());
            entity.setActive(itemForm.isActive());

            itemRepository.save(entity);
        }

        return "redirect:/kolcsonzes";
    }

}
