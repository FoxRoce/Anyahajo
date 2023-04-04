package com.project.anyahajo.controller;

import com.project.anyahajo.auth.AppUserService;
import com.project.anyahajo.form.ItemForm;
import com.project.anyahajo.model.*;
import com.project.anyahajo.repository.UserRepository;
import com.project.anyahajo.repository.ItemRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
//@RequiredArgsConstructor
public class ItemController {


    private ItemRepository itemRepository;

    private UserRepository appUserRepository;

    private AppUserService appUserService;



    public ItemController(ItemRepository itemRepository, UserRepository userRepository, AppUserService appUserService) {
        this.itemRepository = itemRepository;
        this.appUserRepository = userRepository;
        this.appUserService = appUserService;
    }


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

    private record SearchForm(String text, String itemType){};

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
    ) throws IOException {

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
        entity.setPicture(itemForm.getPicture().getBytes());

        itemRepository.save(entity);

        return "redirect:/kolcsonzes";
    }


    @GetMapping("/all-items/img/{id}")
    public ResponseEntity<byte[]> getPictureById(@PathVariable Long id) {
        Optional<Item> item = itemRepository.findById(id);

        if (item.isPresent()) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);

            return new ResponseEntity<>(item.get().getPicture(), headers, HttpStatus.OK);
        }

        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
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
    public String showmodifyItem(@PathVariable("id") String id, Model model) {
        Item item = itemRepository.findByItem_id(Long.parseLong(id));
        model.addAttribute("item", item);
        model.addAttribute("newItem", new ItemForm());
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
        return "item-edit";
    }

    @PostMapping("/admin/termekmodositas/{id}")
    public String modifyItem(@PathVariable("id") String id,
                             Model model,
                             @ModelAttribute("newItem")
                             @Validated
                             ItemForm itemForm,
                             BindingResult bindingResult) throws IOException {

        Item item = itemRepository.findByItem_id(Long.parseLong(id));

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
            return "item-edit";
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
            if (itemForm.getCarrierType() != null) {
                ((Carrier) item).setCarrierType(itemForm.getCarrierType());
            }
            if (!itemForm.getSize().isEmpty()) {
                ((Carrier) item).setSize(itemForm.getSize());
            }
        }


        if (!itemForm.getName().isEmpty()) {
            item.setName(itemForm.getName());
        }
//        if (!itemForm.getAvailability().isEmpty()) {
//            item.setAvailability(itemForm.getAvailability());
//        }
        if (!itemForm.getDescription().isEmpty()) {
            item.setDescription(itemForm.getDescription());
        }
//        if (itemForm.pictureIsEmpty()) {
//            item.setPicture(itemForm.getPicture().getBytes());
//        }
        if (itemForm.getIsActive().describeConstable().isPresent()) {
            item.setActive(itemForm.getIsActive());
        }

        itemRepository.save(item);

        return "redirect:/kolcsonzes";
    }

    @GetMapping(path = {"/kolcsonzes/kolcsonozheto"})
    public String listOnlyRentable(Model model, Principal principal) {
        List<Item> items = itemRepository.findRentable(Availability.Available);
        model.addAttribute("items", items);
        model.addAttribute("searchData", new SearchForm("", ""));
        if (principal != null) {
            User owner = (User) appUserService.loadUserByUsername(principal.getName());
            model.addAttribute("basket", owner.getBasket());
        }
        return "all-items-rentable";
    }
}
