package com.project.anyahajo.controller;

import com.project.anyahajo.auth.AppUserService;
import com.project.anyahajo.form.UserForm;
import com.project.anyahajo.model.Rent;
import com.project.anyahajo.model.Role;
import com.project.anyahajo.model.User;
import com.project.anyahajo.repository.RentRepository;
import com.project.anyahajo.repository.UserRepository;
import com.project.anyahajo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
public class UserController {

    private UserService userService;
    private RentRepository rentRepository;
    private AppUserService userDetailService;
    private final UserRepository userRepository;

    public UserController(UserService userService, RentRepository rentRepository, AppUserService userDetailService,
                          UserRepository userRepository) {
        this.userService = userService;
        this.rentRepository = rentRepository;
        this.userDetailService = userDetailService;
        this.userRepository = userRepository;
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

    @GetMapping("/user/edit")
    public String editUserForm(Principal principal, Model model) {
        User user = (User) userDetailService.loadUserByUsername(principal.getName());
        model.addAttribute("user", user);
        return "user-edit";
    }

    @PostMapping("/user/edit")
    public String updateClub(@Validated @ModelAttribute("user") User user,
                             BindingResult bindingResult,
                             Model model) {

        if(bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            return "user-edit";
        }

        userRepository.save(user);

        return "redirect:/home";
    }

    @GetMapping(path = {"/rents"})
    public String listRents(Model model, Principal principal) {

        User user = (User) userDetailService.loadUserByUsername(principal.getName());

        List<Rent> rents = rentRepository.findByUser(user);
        model.addAttribute("rents", rents);
        return "all-rents";
    }
}
