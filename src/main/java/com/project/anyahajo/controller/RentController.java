package com.project.anyahajo.controller;

import com.project.anyahajo.auth.EmailSender;
import com.project.anyahajo.form.RentForm;
import com.project.anyahajo.model.*;
import com.project.anyahajo.repository.ItemRepository;
import com.project.anyahajo.service.ItemService;
import com.project.anyahajo.service.RentService;
import com.project.anyahajo.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
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
    @NonNull
    private AppUserService userDetailService;
    @NonNull
    private ItemController itemController;

    @GetMapping(path = {"/admin/rents"})
    public String listItems(Model model) {
        List<Rent> rents = rentRepository.findAll();
        model.addAttribute("rents", rents);
        return "all-rents";
    }
    @GetMapping(path = {"/admin/add_new_rent"})
    public String addNewRent(
            @RequestParam(required = false) Long iid,
            Model model
    ) {
        RentForm rf = new RentForm();
        rf.setItem(itemRepository.findByItem_id(iid));
        model.addAttribute("newRent", rf);
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "new-rent";
    }

    @PostMapping("/admin/add_new_rent")
    public String addNewRent(
            @ModelAttribute("newRent") RentForm rentForm,
            @ModelAttribute("new_user_email") String email
    ) {
        Rent newRent = new Rent();

        newRent.setItem(rentForm.getItem());
        rentForm.getItem().setAvailability(Availability.NotAvailable);

        if (email.isEmpty()){
            newRent.setUser(rentForm.getUser());
        } else {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setRole(Role.USER);
            newUser.setLocked(false);
            newUser.setEnabled(true);

            userRepository.save(newUser);
            newRent.setUser(newUser);
        }

        newRent.setStartOfRent(rentForm.getStartOfRent());

        if (rentForm.isExtended()){
            newRent.setEndOfRent(rentForm.getStartOfRent().plusDays(28));
            newRent.setExtended(true);
        } else {
            newRent.setEndOfRent(rentForm.getStartOfRent().plusDays(14));
            newRent.setExtended(false);
        }

        newRent.setPrice(rentForm.getPrice());
        newRent.setDeposit(rentForm.getDeposit());
        newRent.setPayBackAmount(rentForm.getPayBackAmount());

        rentRepository.save(newRent);

        String emailBody = "Kedves vásárló!\n\n" + newRent.getItem().getName() +
                " nevű tárgyra tett kölcsönzése el lett fogadva.\n" +
                "Lejárati dátum: " + newRent.getEndOfRent()  +
                "\nMeghoszabítást igényelhet az alábbi e-mail címen:\n" +
                "admin@gmail.com" +
                "\n\nÜdvözlettel, Anyahajó";

        try {
            emailSender.send(newRent.getUser().getEmail(), emailBody, "Kölcsönzés elfogadva");
        } catch (Exception e){
            System.out.println(emailBody);
        }

        return "redirect:/admin/rents";
    }

    @PostMapping("/rents/{id}/reserve")
    public String updateRentReserve(
            @PathVariable("id") Long id
    ) {
       return "redirect:/admin/add_new_rent?iid={id}";
    }

    @PostMapping("/rents/{id}/accept")
    public String updateRentAccept(
            @PathVariable("id") Long id,
            @ModelAttribute("deposit") Integer deposit,
            Principal principal
    ) {
        User user = (User) userDetailService.loadUserByUsername(principal.getName());
        if (!user.getRole().equals(Role.ADMIN)){
            return "redirect:/rents";
        }

       Rent rent = rentRepository.findByRent_id(id);
       rent.getItem().setAvailability(Availability.NotAvailable);
       rent.setStartOfRent(LocalDate.now());
       rent.setEndOfRent(LocalDate.now().plusDays(14));

       rent.setDeposit(deposit);

        String emailBody = "Kedves vásárló!\n\n" + rent.getItem().getName() +
                " nevű tárgyra tett kölcsönzése el lett fogadva.\n" +
                "Lejárati dátum: " + rent.getEndOfRent()  +
                "\nMeghoszabítást igényelhet az alábbi e-mail címen:\n" +
                "admin@gmail.com" +
                "\n\nÜdvözlettel, Anyahajó";

        try {
            emailSender.send(rent.getUser().getEmail(), emailBody, "Kölcsönzés elfogadva");
        } catch (Exception e) {
            System.out.println(emailBody);
        }
        rentRepository.save(rent);
        return "redirect:/admin/rents";
    }

    @PostMapping("/rents/{id}/decline")
    public String updateRentDecline(
            @PathVariable("id") Long id,
            Principal principal
    ) {
        User user = (User) userDetailService.loadUserByUsername(principal.getName());
        if (!user.getRole().equals(Role.ADMIN)){
            return "redirect:/rents";
        }

        Rent rent = rentRepository.findByRent_id(id);
        rent.getItem().setAvailability(Availability.Available);

        String emailBody = "Kedves vásárló!\n\n" +
                "Sajnálattal közöljük, hogy a "+ rent.getItem().getName() +
                " nevű tárgyra tett kölcsönzése el lett utasítva.\n" +
                "\n\nÜdvözlettel, Anyahajó";

        try {
            emailSender.send(rent.getUser().getEmail(),emailBody,"Kölcsönzés elutasítva");
        } catch (Exception e) {
            System.out.println(emailBody);
        }



    @PostMapping("/kolcsonzes/igenyles")
    public String sendRentDemand(Principal principal) {
        User owner = (User) userService.loadUserByUsername(principal.getName());
    public String sendRentDemand(Principal principal, Model model) {
        User owner = (User) appUserService.loadUserByUsername(principal.getName());
        List<Long> fromBasketRemoveableItems = new ArrayList<>();

        StringBuilder emailBody =
                new StringBuilder("Új foglalás:\n" +
                        "\nNév: " + (owner.getName() == null ? "Nincs nev" : owner.getName().toString())  +
                        "\nE-mail: " + owner.getEmail() +
                        "\n\nFoglalt tárgyak: ");

        for (Long item_id : owner.getBasket()) {
            if (itemService.findByItem_id(item_id) == null) {
                return "error-item-not-found";
            } else if (!itemRepository.findByItem_id(item_id).getAvailability().equals(Availability.Available)) {
                model.addAttribute("termek_neve", itemRepository.findByItem_id(item_id).getName());
                itemController.removeFromBasket(item_id, principal);
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
            emailSender.send(owner.getEmail(),emailBody.toString(),"Foglalas");
            emailSender.send("admin@gmail.com",emailBody.toString(),"Foglalas");
        } catch (Exception e){
            System.out.println(emailBody);
        }
        return "rent-claim-success";
    }

    @GetMapping(path = {"/rents/kikolcsonzott"})
    public String listRentsBorrowedByUser(Model model, Principal principal) {
        User user = (User) userService.loadUserByUsername(principal.getName());
        List<Rent> rents = rentService.findBorrowedByUser_id(user);
        model.addAttribute("rents", rents);
        return "all-rents-by-user";
    }

    @GetMapping(path = {"/rents/jovahagyasravar"})
    public String listRentsWaitAcceptByUser(Model model, Principal principal) {
        User user = (User) userService.loadUserByUsername(principal.getName());
        List<Rent> rents = rentService.findWaitAcceptByUser_id(user);
        model.addAttribute("rents", rents);
        return "all-rents-by-user";
    }

    @GetMapping(path = {"/rents/lejart"})
    public String listRentsExpiredByUser(Model model, Principal principal) {
        User user = (User) userService.loadUserByUsername(principal.getName());
        List<Rent> rents = rentService.findExpiredByUser_id(user);
        model.addAttribute("rents", rents);
        return "all-rents-by-user";
    }

    @GetMapping(path = {"/rents"})
    public String listRents(Model model, Principal principal) {

        User user = (User) userService.loadUserByUsername(principal.getName());

        List<Rent> rents = rentService.findByUser(user);
        model.addAttribute("rents", rents);
        return "all-rents-by-user";
    }

}
