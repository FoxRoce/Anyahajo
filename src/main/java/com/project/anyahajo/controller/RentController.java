package com.project.anyahajo.controller;

import com.project.anyahajo.form.RentForm;
import com.project.anyahajo.model.Availability;
import com.project.anyahajo.model.Rent;
import com.project.anyahajo.repository.RentRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class RentController {

    @NonNull
    RentRepository rentRepository;

    @GetMapping(path = {"/admin/rents"})
    public String listItems(Model model) {
        List<Rent> rents = rentRepository.findAll();
        model.addAttribute("rents", rents);
        return "all-rents";
    }
    @GetMapping(path = {"/admin/add_new_rent"})
    public String addNewRent(Model model) {
        model.addAttribute("newRent", new RentForm());
        return "new-rent";
    }

    @PostMapping("/rents/{id}/reserve")
    public String updateRentReserve(
            @PathVariable("id") Long id
    ) {
        Rent rent = rentRepository.findByRent_id(id);
        rent.getItem().setAvailability(Availability.Reserved);

        System.out.println("Sends e-mail to admin...");

        rentRepository.updateItemByRent_id(rent.getItem(),id);

        return "redirect:/admin/add_new_rent";
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
        rent.setStartOfRent(null);
        rent.setEndOfRent(null);

        System.out.println("Sending e-mail to user...");

        rentRepository.updateItemAndStartOfRentAndEndOfRentByRent_id(rent.getItem(),rent.getStartOfRent(),rent.getEndOfRent(),id);

        return "redirect:/admin/rents";
    }


}
