package com.project.anyahajo.controller;

import com.project.anyahajo.form.UserForm;
import com.project.anyahajo.model.Rent;
import com.project.anyahajo.model.Role;
import com.project.anyahajo.model.User;
import com.project.anyahajo.repository.RentRepository;
import com.project.anyahajo.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
public class UserController {

    private UserService userService;
    private final RentRepository rentRepository;

    public UserController(UserService userService,
                          RentRepository rentRepository) {
        this.userService = userService;
        this.rentRepository = rentRepository;
    }

    @GetMapping(path = {"/admin/users"})
    public String listItems(Model model) {
        List<UserForm> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "all-users";
    }

    @GetMapping(path = {"/admin/users/{id}"})
    public String showUserDetails(
            Model model,
            @PathVariable("id") Long id
    ) {
        User user = userService.findUserByUser_id(id);
        model.addAttribute("user", user);

        List<Rent> rents = rentRepository.findByUser_id(user);
        model.addAttribute("rents", rents);
        return "user-detail";
    }

    @PostMapping("/users/{id}/role")
    public String updateUserRole(
            @PathVariable("id") Long id
    ) {
        User user =(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getUser_id().equals(id)){
            return "redirect:/admin/users";
        }

        if (userService.findUserByUser_id(id).getRole().equals(Role.USER)){
            userService.updateUserRole(id,Role.ADMIN);
        } else {
            userService.updateUserRole(id,Role.USER);
        }

        return "redirect:/admin/users";
    }
}
