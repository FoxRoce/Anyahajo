package com.project.anyahajo.controller;

import com.project.anyahajo.auth.AppUserService;
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
    RentRepository rentRepository;
    @NonNull
    ItemRepository itemRepository;
    @NonNull
    UserRepository userRepository;
    @NonNull AppUserService appUserService;

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

        rentRepository.updateItemAndStartOfRentAndEndOfRentByRent_id(rent.getItem(),rent.getStartOfRent(),rent.getEndOfRent(),id);

        return "redirect:/admin/rents";
    }

    @PostMapping("/rents/{id}/decline")
    public String updateRentDecline(
            @PathVariable("id") Long id
    ) {
        Rent rent = rentRepository.findByRent_id(id);
        rent.getItem().setAvailability(Availability.Available);

        System.out.println("Sending e-mail to user...");

        rentRepository.deleteById(id);
        return "redirect:/admin/rents";
    }

//    @GetMapping("/asd")
//    public String addItemsToReserve(
//            @ModelAttribute("items") List<Item> items
//    ){
//        List<String> emailMessage = new ArrayList<>();
//        User user =(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//
//        emailMessage.add(user.getName() + " lefoglalt targyai: ");
//
//        for (Item item : items) {
//            item.setAvailability(Availability.Reserved);
//            emailMessage.add(item.getName());
//        }
//
//        List sendEmail = emailMessage;
//
//        return "redirect: /userProfile";
//    }

    @PostMapping("/rents/{id}/back")
    public String updateRentBroughtBack(
            @PathVariable("id") Long id,
            @ModelAttribute("historyDate") LocalDate date
    ) {
        Rent rent = rentRepository.findByRent_id(id);
        rent.getItem().setAvailability(Availability.Available);
        rent.setHistory(date);

        System.out.println("Sending e-mail to user...");

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

        System.out.println("Sending e-mail to user...");

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

        System.out.println("Sending e-mail to user...");

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

        System.out.println("Sending e-mail to user...");

        rentRepository.save(rent);

        return "redirect:/admin/rents";
    }
    @PostMapping("/kolcsonzes/igenyles")
    public String sendRentDemand(Principal principal) {
        User owner = (User) appUserService.loadUserByUsername(principal.getName());
        List<Long> fromBasketRemoveableItems = new ArrayList<>();
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
                itemRepository.save(item);
            }
        }
        for (int i = 0; i < fromBasketRemoveableItems.size(); i++) {
            owner.getBasket().remove(fromBasketRemoveableItems.get(i));
        }
        userRepository.save(owner);
        return "basket";
    }
}
