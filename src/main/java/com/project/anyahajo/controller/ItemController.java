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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDate;
import java.util.*;

@Controller
public class ItemController {

    @NonNull
    private ItemRepository itemRepository;
    @NonNull
    private UserRepository appUserRepository;
    @NonNull
    private AppUserService appUserService;

    private static final String FOLDER_PATH = "/images/";

    public ItemController(ItemRepository itemRepository, UserRepository appUserRepository, AppUserService appUserService) {
        this.itemRepository = itemRepository;
        this.appUserRepository = appUserRepository;
        this.appUserService = appUserService;
        if (!Files.exists(Paths.get(FOLDER_PATH))) {
            try {
                Files.createDirectories(Paths.get(FOLDER_PATH));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @GetMapping(path = {"/kolcsonzes"})
    public String listItems(Model model, Principal principal) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
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
            BindingResult bindingResult,
            @RequestParam("file") MultipartFile file
    ) {
        Path folderPath = Paths.get("/images/");
        if (!Files.exists(folderPath)) {
            try {
                Files.createDirectories(folderPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

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
        entity.setActive(itemForm.getIsActive());

        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                Path path = Paths.get("/images/" + file.getOriginalFilename());
                Files.write(path, bytes);
                entity.setPicture(Arrays.toString(path.toString().getBytes()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        itemRepository.save(entity);

        return "redirect:/kolcsonzes";
    }
        @GetMapping("/item/{id}")
    public String getItem(@PathVariable("id") String id, Model model, Principal principal) {
        Item item = itemRepository.findByItem_id(Long.parseLong(id));
        if(principal != null && item != null) {
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
        List <Item> itemsInTheBasket = owner.getBasket().stream().map(x -> {
            if(itemRepository.findById(x).isPresent()) {
                return itemRepository.findById(x).get();
            } else {
                return null;
            }
        }).toList();
        model.addAttribute("itemsInTheBasket", itemsInTheBasket);
        return "basket";
    }




}
