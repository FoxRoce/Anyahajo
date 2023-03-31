package com.project.anyahajo.controller;

import com.project.anyahajo.auth.AppUserService;
import com.project.anyahajo.auth.EmailSender;
import com.project.anyahajo.form.RentForm;
import com.project.anyahajo.form.UserForm;
import com.project.anyahajo.model.*;
import com.project.anyahajo.repository.ItemRepository;
import com.project.anyahajo.repository.RentRepository;
import com.project.anyahajo.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private RentRepository rentRepository;
    @NonNull
    private ItemRepository itemRepository;
    @NonNull
    private UserRepository userRepository;
    @NonNull
    private AppUserService appUserService;
    @NonNull
    private EmailSender emailSender;

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
            @PathVariable("id") Long id
    ) {
       Rent rent = rentRepository.findByRent_id(id);
       rent.getItem().setAvailability(Availability.NotAvailable);
       rent.setStartOfRent(LocalDate.now());
       rent.setEndOfRent(LocalDate.now().plusDays(14));

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
            @PathVariable("id") Long id
    ) {
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

        rentRepository.save(rent);

        rentRepository.deleteById(id);
        return "redirect:/admin/rents";
    }


    @PostMapping("/rents/{id}/back")
    public String updateRentBroughtBack(
            @PathVariable("id") Long id,
            @ModelAttribute("historyDate") LocalDate date
    ) {
        Rent rent = rentRepository.findByRent_id(id);
        rent.getItem().setAvailability(Availability.Available);
        rent.setHistory(date);

        String emailBody = "Kedves vásárló!\n\n"
                + rent.getItem().getName() + " nevű tárgyra vissza lett hozva." +
                "\nKöszönjük kölcsönzését!" +
                "\n\nÜdvözlettel, Anyahajó";

        try {
            emailSender.send(rent.getUser().getEmail(),emailBody,"Kölcsönzés vége");
        } catch (Exception e){
            System.out.println(emailBody);
        }

        rentRepository.save(rent);

        return "redirect:/admin/rents";
    }

    @PostMapping("/rents/{id}/extend")
    public String updateRentExtend(
            @PathVariable("id") Long id
    ) {
        Rent rent = rentRepository.findByRent_id(id);
        rent.setEndOfRent(rent.getEndOfRent().plusDays(14));
        rent.setExtended(true);

        String emailBody = "Kedves vásárló!\n\n" + rent.getItem().getName() +
                " nevű tárgyra tett kölcsönzés lejárati ideje meg lett hosszabítva.\n" +
                "Új lejárati dátum: " + rent.getEndOfRent()  +
                "\n\nÜdvözlettel, Anyahajó";

        try {
            emailSender.send(rent.getUser().getEmail(), emailBody, "Lejárati dátum meghosszabítás");
        } catch (Exception e){
            System.out.println(emailBody);
        }

        rentRepository.save(rent);

        return "redirect:/admin/rents";
    }

    @PostMapping("/rents/{id}/changeEndDate")
    public String updateRentChangeEndDate(
            @PathVariable("id") Long id,
            @ModelAttribute("newEndDate") LocalDate date
    ) {
        Rent rent = rentRepository.findByRent_id(id);
        rent.setEndOfRent(date);

        if (rent.getEndOfRent().isAfter(rent.getStartOfRent().plusDays(14))) {
            rent.setExtended(true);
        } else {
            rent.setExtended(false);
        }
        String emailBody = "Kedves vásárló!\n\n" + rent.getItem().getName() +
                " nevű tárgyra tett kölcsönzés lejárati ideje megváltozott.\n" +
                "Új lejárati dátum: " + rent.getEndOfRent()  +
                "\nMegértését köszönjük." +
                "\n\nÜdvözlettel, Anyahajó";

        try {
            emailSender.send(rent.getUser().getEmail(),emailBody,"Lejárati dátum módosítás");
        } catch (Exception e){
            System.out.println(emailBody);

        }

        rentRepository.save(rent);

        return "redirect:/admin/rents";
    }

    @PostMapping("/rents/{id}/changeStartDate")
    public String updateRentChangeStartDate(
            @PathVariable("id") Long id,
            @ModelAttribute("newStartDate") LocalDate date
    ) {
        Rent rent = rentRepository.findByRent_id(id);
        rent.setStartOfRent(date);
        rent.setEndOfRent(date.plusDays(14));

        rentRepository.save(rent);

        return "redirect:/admin/rents";
    }
    @PostMapping("/kolcsonzes/igenyles")
    public String sendRentDemand(Principal principal) {
        User owner = (User) appUserService.loadUserByUsername(principal.getName());
        List<Long> fromBasketRemoveableItems = new ArrayList<>();

        StringBuilder emailBody =
                new StringBuilder("Új foglalás:\n" +
                        "\nNév: " + owner.getName().toString() +
                        "\nE-mail: " + owner.getEmail() +
                        "\n\nFoglalt tárgyak: ");

        for (Long item_id : owner.getBasket()) {
            if (itemRepository.findByItem_id(item_id) == null) {
                return "error-item-not-found";
            } else {
                Item item = itemRepository.findByItem_id(item_id);
                Rent rent = new Rent();
                rent.setItem(item);
                rent.setUser(owner);
                item.setAvailability(Availability.Reserved);
                fromBasketRemoveableItems.add(item_id);
                rentRepository.save(rent);
                emailBody.append("\n").append(item.getName());
            }
        }
        for (int i = 0; i < fromBasketRemoveableItems.size(); i++) {
            owner.getBasket().remove(fromBasketRemoveableItems.get(i));
        }
        userRepository.save(owner);

        emailBody.append("\n\nKöszönjük foglalását!\n\nÜdvözlettel, Anyahajó");

        try {
            emailSender.send(owner.getEmail(),emailBody.toString(),"Foglalas");
            emailSender.send("admin@gmail.com",emailBody.toString(),"Foglalas");
        } catch (Exception e){
            System.out.println(emailBody);
        }


        return "basket";
    }
}
