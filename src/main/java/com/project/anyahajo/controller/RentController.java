package com.project.anyahajo.controller;

import com.project.anyahajo.auth.EmailSender;
import com.project.anyahajo.form.RentForm;
import com.project.anyahajo.model.*;
import com.project.anyahajo.repository.ItemRepository;
import com.project.anyahajo.repository.RentRepository;
import com.project.anyahajo.repository.UserRepository;
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
    private RentRepository rentRepository;
    @NonNull
    private ItemRepository itemRepository;
    @NonNull
    private UserService userService;
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
        List<User> users = userService.findAll();
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
        User user = (User) userService.loadUserByUsername(principal.getName());
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
        User user = (User) userService.loadUserByUsername(principal.getName());
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

        rentRepository.save(rent);

        rentRepository.deleteById(id);
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

        Rent rent = rentRepository.findByRent_id(id);
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

        rentRepository.save(rent);

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
            @ModelAttribute("newEndDate") LocalDate date,
            Principal principal
    ) {
        User user = (User) userService.loadUserByUsername(principal.getName());
        if (!user.getRole().equals(Role.ADMIN)){
            return "redirect:/rents";
        }

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
            @ModelAttribute("newStartDate") LocalDate date,
            Principal principal
    ) {
        User user = (User) userService.loadUserByUsername(principal.getName());
        if (!user.getRole().equals(Role.ADMIN)){
            return "redirect:/rents";
        }

        Rent rent = rentRepository.findByRent_id(id);
        rent.setStartOfRent(date);
        rent.setEndOfRent(date.plusDays(14));

        rentRepository.save(rent);

        return "redirect:/admin/rents";
    }
    @PostMapping("/kolcsonzes/igenyles")
    public String sendRentDemand(Principal principal) {
        User owner = (User) userService.loadUserByUsername(principal.getName());
        List<Long> fromBasketRemoveableItems = new ArrayList<>();

        StringBuilder emailBody =
                new StringBuilder("Új foglalás:\n" +
                        "\nNév: " + (owner.getName() == null ? "Nincs nev" : owner.getName().toString())  +
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
                rent.setPrice(0);
                rent.setDeposit(0);
                rent.setPayBackAmount(0);
                rentRepository.save(rent);
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


        return "basket";
    }

    @GetMapping(path = {"/admin/rents/kikolcsonzott"})
    public String listRentsBorrowed(Model model) {
        List<Rent> rents = rentRepository.findBorrowed();
        model.addAttribute("rents", rents);
        return "all-rents";
    }

    @GetMapping(path = {"/admin/rents/jovahagyasravar"})
    public String listRentsWaitAccept(Model model) {
        List<Rent> rents = rentRepository.findWaitAccept();
        model.addAttribute("rents", rents);
        return "all-rents";
    }

    @GetMapping(path = {"/admin/rents/lejart"})
    public String listRentsExpired(Model model) {
        List<Rent> rents = rentRepository.findExpired();
        model.addAttribute("rents", rents);
        return "all-rents";
    }

    @GetMapping(path = {"/rents/kikolcsonzott"})
    public String listRentsBorrowedByUser(Model model, Principal principal) {
        User user = (User) userService.loadUserByUsername(principal.getName());
        List<Rent> rents = rentRepository.findBorrowedByUser_id(user);
        model.addAttribute("rents", rents);
        return "all-rents-by-user";
    }

    @GetMapping(path = {"/rents/jovahagyasravar"})
    public String listRentsWaitAcceptByUser(Model model, Principal principal) {
        User user = (User) userService.loadUserByUsername(principal.getName());
        List<Rent> rents = rentRepository.findWaitAcceptByUser_id(user);
        model.addAttribute("rents", rents);
        return "all-rents-by-user";
    }

    @GetMapping(path = {"/rents/lejart"})
    public String listRentsExpiredByUser(Model model, Principal principal) {
        User user = (User) userService.loadUserByUsername(principal.getName());
        List<Rent> rents = rentRepository.findExpiredByUser_id(user);
        model.addAttribute("rents", rents);
        return "all-rents-by-user";
    }

}
