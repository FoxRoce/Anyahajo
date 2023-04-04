package com.project.anyahajo.controller;

import com.project.anyahajo.auth.EmailSender;
import com.project.anyahajo.model.*;
import com.project.anyahajo.service.ItemService;
import com.project.anyahajo.service.RentService;
import com.project.anyahajo.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class RentController {

    @NonNull
    private RentService rentService;
    @NonNull
    private ItemService itemService;
    @NonNull
    private UserService userService;
    @NonNull
    private EmailSender emailSender;


    @PostMapping("/kolcsonzes/igenyles")
    public String sendRentDemand(
            Principal principal,
            Model model
    ) {
        User owner = (User) userService.loadUserByUsername(principal.getName());
        List<Long> fromBasketRemoveableItems = new ArrayList<>();

        StringBuilder emailBody =
                new StringBuilder("Új foglalás:\n" +
                        "\nNév: " + (owner.getName() == null ? "Nincs nev" : owner.getName().toString()) +
                        "\nE-mail: " + owner.getEmail() +
                        "\n\nFoglalt tárgyak: ");

        for (Long item_id : owner.getBasket()) {
            if (itemService.findByItem_id(item_id) == null) {
                return "error-item-not-found";
            } else if (!itemService.findByItem_id(item_id).getAvailability().equals(Availability.Available)) {
                model.addAttribute("termek_neve", itemService.findByItem_id(item_id).getName());
                removeFromBasket(item_id, principal);
                return "error-item-is-not-available";
            } else {
                Item item = itemService.findByItem_id(item_id);
                Rent rent = new Rent();
                rent.setItem(item);
                rent.setUser(owner);
                item.setAvailability(Availability.Reserved);
                fromBasketRemoveableItems.add(item_id);
                rent.setPrice(0);
                rent.setDeposit(0);
                rent.setPayBackAmount(0);
                rentService.save(rent);
                emailBody.append("\n").append(item.getName());
            }
        }
        for (int i = 0; i < fromBasketRemoveableItems.size(); i++) {
            owner.getBasket().remove(fromBasketRemoveableItems.get(i));
        }
        userService.save(owner);

        emailBody.append("\n\nKöszönjük foglalását!\n\nÜdvözlettel, Anyahajó");

        try {
            emailSender.send(owner.getEmail(), emailBody.toString(), "Foglalas");
            emailSender.send("admin@gmail.com", emailBody.toString(), "Foglalas");
        } catch (Exception e) {
            System.out.println(emailBody);
        }
        return "rent-claim-success";
    }

    @PostMapping("kosar/delete")
    public String removeFromBasket(
            @RequestParam("item_id") Long id,
            Principal principal
    ) {
        User owner = (User) userService.loadUserByUsername(principal.getName());
        owner.getBasket().remove(id);
        userService.save(owner);
        return "redirect:/kosar";
    }

    @GetMapping(path = {"/rents/kikolcsonzott"})
    public String listRentsBorrowedByUser(
            Model model,
            Principal principal
    ) {
        User user = (User) userService.loadUserByUsername(principal.getName());
        List<Rent> rents = rentService.findBorrowedByUser_id(user);
        model.addAttribute("rents", rents);
        return "all-rents-by-user";
    }

    @GetMapping(path = {"/rents/jovahagyasravar"})
    public String listRentsWaitAcceptByUser(
            Model model,
            Principal principal
    ) {
        User user = (User) userService.loadUserByUsername(principal.getName());
        List<Rent> rents = rentService.findWaitAcceptByUser_id(user);
        model.addAttribute("rents", rents);
        return "all-rents-by-user";
    }

    @GetMapping(path = {"/rents/lejart"})
    public String listRentsExpiredByUser(
            Model model,
            Principal principal
    ) {
        User user = (User) userService.loadUserByUsername(principal.getName());
        List<Rent> rents = rentService.findExpiredByUser_id(user);
        model.addAttribute("rents", rents);
        return "all-rents-by-user";
    }

    @GetMapping(path = {"/rents"})
    public String listRents(
            Model model,
            Principal principal
    ) {
        User user = (User) userService.loadUserByUsername(principal.getName());
        List<Rent> rents = rentService.findByUser(user);
        model.addAttribute("rents", rents);
        return "all-rents-by-user";
    }

}