package com.project.anyahajo.controller;

import com.project.anyahajo.auth.EmailSender;
import com.project.anyahajo.form.ItemForm;
import com.project.anyahajo.form.RentForm;
import com.project.anyahajo.form.UserForm;
import com.project.anyahajo.model.*;
import com.project.anyahajo.service.ItemService;
import com.project.anyahajo.service.RentService;
import com.project.anyahajo.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    @NonNull
    private ItemService itemService;
    @NonNull
    private UserService userService;
    @NonNull
    private RentService rentService;
    @NonNull
    private EmailSender emailSender;



    @GetMapping("")
    public String getAdminPage(Principal principal) {
        User user = (User) userService.loadUserByUsername(principal.getName());
        if(user.getRole().equals(Role.ADMIN)) {
            return "admin";
        } else {
            return "home";
        }
    }

    @GetMapping(path = {"/users"})
    public String listUsers(Model model) {
        List<UserForm> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "all-users";
    }

    @GetMapping(path = {"/users/{id}"})
    public String showUserDetails(
            Model model,
            @PathVariable("id") Long id
    ) {
        User user = userService.findUserByUser_id(id);
        model.addAttribute("user", user);

        List<Rent> rents = rentService.findByUser(user);
        model.addAttribute("rents", rents);
        return "user-detail";
    }


    @GetMapping(path = {"/ujTargyFelvetel"})
    public String newItem(Model model) {
        model.addAttribute("newItem", new ItemForm());
        return "admin-add-item";
    }

    @PostMapping(path = {"/ujTargyFelvetel"})
    public String saveItem(
            @ModelAttribute("newItem")
            @Validated
            ItemForm itemForm,
            BindingResult bindingResult
    ) {
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

        itemService.save(entity);

        return "redirect:/kolcsonzes";
    }

    @GetMapping(path = {"/rents"})
    public String listRents(Model model) {
        List<Rent> rents = rentService.findAll();
        model.addAttribute("rents", rents);
        return "all-rents";
    }
    @GetMapping(path = {"/add_new_rent"})
    public String addNewRent(
            @RequestParam(required = false) Long iid,
            Model model
    ) {
        RentForm rf = new RentForm();
        rf.setItem(itemService.findByItem_id(iid));
        model.addAttribute("newRent", rf);
        List<Item> items = itemService.findAll();
        model.addAttribute("items", items);
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "new-rent";
    }

    @PostMapping("/add_new_rent")
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

            userService.save(newUser);
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

        rentService.save(newRent);

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
        User user = (User) userService.loadUserByUsername(principal.getName());
        if (!user.getRole().equals(Role.ADMIN)){
            return "redirect:/rents";
        }

        Rent rent = rentService.findByRent_id(id);
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
        rentService.save(rent);
        return "redirect:/admin/rents";
    }

    @PostMapping("/rents/{id}/decline")
    public String updateRentDecline(
            @PathVariable("id") Long id,
            Principal principal
    ) {
        User user = (User) userService.loadUserByUsername(principal.getName());
        if (!user.getRole().equals(Role.ADMIN)){
            return "redirect:/rents";
        }

        Rent rent = rentService.findByRent_id(id);
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

        rentService.save(rent);

        rentService.deleteById(id);
        return "redirect:/admin/rents";
    }


    @PostMapping("/rents/{id}/back")
    public String updateRentBroughtBack(
            @PathVariable("id") Long id,
            @ModelAttribute("historyDate") LocalDate date,
            @ModelAttribute("price") Integer price,
            Principal principal
    ) {
        User user = (User) userService.loadUserByUsername(principal.getName());
        if (!user.getRole().equals(Role.ADMIN)){
            return "redirect:/rents";
        }

        Rent rent = rentService.findByRent_id(id);
        rent.getItem().setAvailability(Availability.Available);
        rent.setHistory(date);

        rent.setPrice(price);

        if (rent.getDeposit() - rent.getPrice() >= 0){
            rent.setPayBackAmount(rent.getDeposit() - rent.getPrice());
        } else {
            rent.setPayBackAmount(0);
        }

        String emailBody = "Kedves vásárló!\n\n"
                + rent.getItem().getName() + " nevű tárgyra vissza lett hozva." +
                "\nKöszönjük kölcsönzését!" +
                "\n\nÜdvözlettel, Anyahajó";

        try {
            emailSender.send(rent.getUser().getEmail(),emailBody,"Kölcsönzés vége");
        } catch (Exception e){
            System.out.println(emailBody);
        }

        rentService.save(rent);

        return "redirect:/admin/rents";
    }

    @PostMapping("/rents/{id}/extend")
    public String updateRentExtend(
            @PathVariable("id") Long id,
            Principal principal
    ) {
        User user = (User) userService.loadUserByUsername(principal.getName());
        if (!user.getRole().equals(Role.ADMIN)){
            return "redirect:/rents";
        }

        Rent rent = rentService.findByRent_id(id);
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

        rentService.save(rent);

        return "redirect:/admin/rents";
    }

    @PostMapping("/rents/{id}/changeEndDate")
    public String updateRentChangeEndDate(
            @PathVariable("id") Long id,
            @ModelAttribute("newEndDate") LocalDate date,
            Principal principal
    ) {
        User user = (User) userService.loadUserByUsername(principal.getName());
        if (!user.getRole().equals(Role.ADMIN)){
            return "redirect:/rents";
        }

        Rent rent = rentService.findByRent_id(id);
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

        rentService.save(rent);

        return "redirect:/admin/rents";
    }

    @PostMapping("/rents/{id}/changeStartDate")
    public String updateRentChangeStartDate(
            @PathVariable("id") Long id,
            @ModelAttribute("newStartDate") LocalDate date,
            Principal principal
    ) {
        User user = (User) userService.loadUserByUsername(principal.getName());
        if (!user.getRole().equals(Role.ADMIN)){
            return "redirect:/rents";
        }

        Rent rent = rentService.findByRent_id(id);
        rent.setStartOfRent(date);
        rent.setEndOfRent(date.plusDays(14));

        rentService.save(rent);

        return "redirect:/admin/rents";
    }

    @GetMapping(path = {"/rents/kikolcsonzott"})
    public String listRentsBorrowed(Model model) {
        List<Rent> rents = rentService.findBorrowed();
        model.addAttribute("rents", rents);
        return "all-rents";
    }

    @GetMapping(path = {"/rents/jovahagyasravar"})
    public String listRentsWaitAccept(Model model) {
        List<Rent> rents = rentService.findWaitAccept();
        model.addAttribute("rents", rents);
        return "all-rents";
    }

    @GetMapping(path = {"/rents/lejart"})
    public String listRentsExpired(Model model) {
        List<Rent> rents = rentService.findExpired();
        model.addAttribute("rents", rents);
        return "all-rents";
    }



}
