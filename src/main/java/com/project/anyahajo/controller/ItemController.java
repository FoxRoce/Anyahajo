package com.project.anyahajo.controller;

import com.project.anyahajo.auth.AppUserService;
import com.project.anyahajo.form.ItemForm;
import com.project.anyahajo.model.*;
import com.project.anyahajo.repository.UserRepository;
import com.project.anyahajo.repository.ItemRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ItemController {

    @NonNull
    private ItemRepository itemRepository;
    @NonNull
    private UserRepository appUserRepository;
    @NonNull
    private AppUserService appUserService;

    @GetMapping(path = {"/kolcsonzes"})
    public String listItems(Model model, Principal principal) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        model.addAttribute("searchData", new SearchForm("", ""));
        if (principal != null) {
            User owner = (User) appUserService.loadUserByUsername(principal.getName());
            model.addAttribute("basket", owner.getBasket());
        }
        return "all-items";
    }

    @GetMapping(path = {"/books"})
    public String listItems() {
        return "redirect:/kolcsonzes?itemType=Book";
    }

    private record SearchForm(String text, String itemType) {
    }

    ;

    @PostMapping("/kolcsonzes/kereses")
    public String searchItems(
            Model model,
            @ModelAttribute("searchData") SearchForm form,
            Principal principal
    ) throws ClassNotFoundException {
        List<Item> items;
        if (Objects.equals(form.itemType, "") || Objects.equals(form.itemType, "*")) {
            items = itemRepository.findByText(form.text);
        } else {
            items = itemRepository.findBySearch(form.text, Class.forName("com.project.anyahajo.model." + form.itemType));
        }
        model.addAttribute("items", items);
        if (principal != null) {
            User owner = (User) appUserService.loadUserByUsername(principal.getName());
            model.addAttribute("basket", owner.getBasket());
        }
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
    public String getItem(@PathVariable("id") String id, Model model, Principal principal) {
        Item item = itemRepository.findByItem_id(Long.parseLong(id));
        if (principal != null && item != null) {
            model.addAttribute("item", item);
            User owner = (User) appUserService.loadUserByUsername(principal.getName());
            model.addAttribute("basket", owner.getBasket());
            return "item";
        }
        if (item != null) {
            model.addAttribute("item", item);
            return "item";
        }
        return "redirect:/kolcsonzes";
    }

    @PostMapping(path = {"/kolcsonzes/putInTheBasket", "/kolcsonzes/kereses/putInTheBasket"})
    public String putInTheBasket(@RequestParam("item_id") Long id, Principal principal) {
        User owner = (User) appUserService.loadUserByUsername(principal.getName());
        Item item = itemRepository.findByItem_id(id);
        owner.getBasket().add(id);
        appUserRepository.save(owner);
        return "redirect:/kolcsonzes";
    }

    @PostMapping("kosar/delete")
    public String removeFromBasket(@RequestParam("item_id") Long id, Principal principal) {
        User owner = (User) appUserService.loadUserByUsername(principal.getName());
        owner.getBasket().remove(id);
        appUserRepository.save(owner);
        return "redirect:/kosar";
    }

    @GetMapping(path = "/kosar")
    public String showBasket(Principal principal, Model model) {
        User owner = (User) appUserService.loadUserByUsername(principal.getName());
        List<Item> itemsInTheBasket = owner.getBasket().stream().map(x -> {
            if (itemRepository.findById(x).isPresent()) {
                return itemRepository.findById(x).get();
            } else {
                return null;
            }
        }).toList();
        model.addAttribute("itemsInTheBasket", itemsInTheBasket);
        return "basket";
    }

    @GetMapping("/admin/termekmodositas/{id}")
    public String showmodifyItem(@PathVariable("id") Long id, Model model) {
        Item item = itemRepository.findByItem_id(id);
        model.addAttribute("item", item);
        model.addAttribute("newItem", new ItemForm());
        String itemType = String.valueOf(item.getClass()).toLowerCase();
        model.addAttribute("itemType", itemType);
        return "item-edit2";
    }

    @PostMapping("/admin/termekmodositas/{id}")
    public String modifyItem(@PathVariable("id") Long id,
                             Model model,
                             @ModelAttribute("newItem")
                             @Validated
                             ItemForm itemForm,
                             BindingResult bindingResult) {

        Item item = itemRepository.findByItem_id(id);
        model.addAttribute("item", item);
        String itemType = String.valueOf(item.getClass()).toLowerCase();
        switch (itemType) {
            case "class com.project.anyahajo.model.book":
                itemType = "book";
                break;
            case "class com.project.anyahajo.model.carrier":
                itemType = "carrier";
                break;
            case "class com.project.anyahajo.model.babycare":
                itemType = "babycare";
                break;
        }
        model.addAttribute("itemType", itemType);

        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult);
            return "admin-add-item";
        }
        if (item instanceof Babycare) {
            if (!itemForm.getBabycareBrand().isEmpty()) {
                ((Babycare) item).setBabycareBrand(itemForm.getBabycareBrand());
            }
        } else if (item instanceof Book) {
            if (!itemForm.getAuthor().isEmpty()) {
                ((Book) item).setAuthor(itemForm.getAuthor());
            }
        } else {
            if (!itemForm.getCarrierBrand().isEmpty()) {
                ((Carrier) item).setCarrierBrand(itemForm.getCarrierBrand());
            }
            if (!itemForm.getCarrierType().isEmpty()) {
                ((Carrier) item).setCarrierType(itemForm.getCarrierType());
            }
            if (!itemForm.getSize().isEmpty()) {
                ((Carrier) item).setSize(itemForm.getSize());
            }
        }

        if (!itemForm.getName().isEmpty()) {
            item.setName(itemForm.getName());
        }
        if (!itemForm.getAvailability().isEmpty()) {
            item.setAvailability(itemForm.getAvailability());
        }
        if (!itemForm.getDescription().isEmpty()) {
            item.setDescription(itemForm.getDescription());
        }
        if (itemForm.pictureIsEmpty()) {
            item.setPicture(itemForm.getPicture());
        }
        if (itemForm.getIsActive().describeConstable().isPresent()) {
            item.setActive(itemForm.getIsActive());
        }

        itemRepository.save(item);

        return "redirect:/item";
    }
}
