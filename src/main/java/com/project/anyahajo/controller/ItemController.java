package com.project.anyahajo.controller;

import com.project.anyahajo.model.*;
import com.project.anyahajo.repository.UserRepository;
import com.project.anyahajo.repository.ItemRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.project.anyahajo.service.ItemService;
import com.project.anyahajo.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ItemController {

    @NonNull
    private ItemService itemService;
    @NonNull
    private UserService userService;
    private record SearchForm(String text, String itemType){};



    @GetMapping(path = {"/kolcsonzes"})
    public String listItems(
            Model model,
            Principal principal
    ) {
        List<Item> items = itemService.findAll();
        model.addAttribute("items", items);
        model.addAttribute("searchData", new SearchForm("", ""));
        if (principal != null) {
            User owner = (User) userService.loadUserByUsername(principal.getName());
            model.addAttribute("basket", owner.getBasket());
        }
        return "all-items";
    }

    @GetMapping(path = {"/books"})
    public String listItems() {
        return "redirect:/kolcsonzes?itemType=Book";
    }

    @PostMapping("/kolcsonzes/kereses")
    public String searchItems(
            Model model,
            @ModelAttribute("searchData") SearchForm form,
            Principal principal
    ) throws ClassNotFoundException {
        List<Item> items;
        if (Objects.equals(form.itemType, "") || Objects.equals(form.itemType, "*")) {
            items = itemService.findByText(form.text);
        } else {
            items = itemService.findBySearch(form.text, Class.forName("com.project.anyahajo.model." + form.itemType));
        }
        model.addAttribute("items", items);
        if (principal != null) {
            User owner = (User) userService.loadUserByUsername(principal.getName());
            model.addAttribute("basket", owner.getBasket());
        }
        return "all-items";
    }



    @GetMapping("/all-items/img/{id}")
    public ResponseEntity<byte[]> getPictureById(
            @PathVariable Long id
    ) {
        Optional<Item> item = itemService.findById(id);

        if (item.isPresent()) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);

            return new ResponseEntity<>(item.get().getPicture(), headers, HttpStatus.OK);
        }

        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/item/{id}")
    public String getItem(
            @PathVariable("id") String id,
            Model model,
            Principal principal
    ) {
        Item item = itemService.findByItem_id(Long.parseLong(id));
        if(principal != null && item != null) {
            model.addAttribute("item", item);
            User owner = (User) userService.loadUserByUsername(principal.getName());
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
    public String putInTheBasket(
            @RequestParam("item_id") Long id,
            Principal principal
    ) {
        User owner = (User) userService.loadUserByUsername(principal.getName());
            owner.getBasket().add(id);
            userService.save(owner);
        return "redirect:/kolcsonzes";
    }


    @GetMapping(path = "/kosar")
    public String showBasket(
            Principal principal,
            Model model
    ) {
        User owner = (User) userService.loadUserByUsername(principal.getName());
        List <Item> itemsInTheBasket = owner.getBasket().stream().map(x -> {
            if(itemService.findById(x).isPresent()) {
                return itemService.findById(x).get();
            } else {
                return null;
            }
        }).toList();
        model.addAttribute("itemsInTheBasket", itemsInTheBasket);
        return "basket";
    }


    @GetMapping(path = {"/kolcsonzes/kolcsonozheto"})
    public String listOnlyRentable(Model model, Principal principal) {
        List<Item> items = itemService.findRentable(Availability.Available);
        model.addAttribute("items", items);
        model.addAttribute("searchData", new SearchForm("", ""));
        if (principal != null) {
            User owner = (User) userService.loadUserByUsername(principal.getName());
            model.addAttribute("basket", owner.getBasket());
        }
        return "all-items-rentable";
    }
}
