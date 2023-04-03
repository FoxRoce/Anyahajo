package com.project.anyahajo.controller;

import com.project.anyahajo.form.UserForm;
import com.project.anyahajo.model.Rent;
import com.project.anyahajo.model.Role;
import com.project.anyahajo.model.User;
import com.project.anyahajo.repository.RentRepository;
import com.project.anyahajo.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {

    @NonNull
    private UserService userService;
    @NonNull
    private RentRepository rentRepository;
    @NonNull
    private PasswordEncoder passwordEncoder;

    @GetMapping("/admin")
    public String getAdminPage(Principal principal) {
        User user = (User) userService.loadUserByUsername(principal.getName());
        if(user.getRole().equals(Role.ADMIN)) {
            return "admin";
        } else {
            return "home";
        }
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
        User userGet = (User) userService.loadUserByUsername(principal.getName());
        UserForm user = userService.mapToUserForm(userGet);
        model.addAttribute("user", user);
        return "user-edit";
    }

    @PostMapping("/user/edit")
    public String updateClub(@Validated @ModelAttribute("user") UserForm user,
                             BindingResult bindingResult,
                             Model model,
                             Principal principal) {

        if(bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            return "user-edit";
        }

        User userGet = (User) userService.loadUserByUsername(principal.getName());
        UserForm oldUser = userService.mapToUserForm(userGet);

        if (!user.getName().getFirstName().isEmpty() || !user.getName().getLastName().isEmpty()){
            oldUser.setName(user.getName());
        }
        if (!user.getEmail().isEmpty()){
            oldUser.setEmail(user.getEmail());
        }
        if (!user.getPhoneNumber().isEmpty()){
            oldUser.setPhoneNumber(user.getPhoneNumber());
        }
        if (!user.getPassword().isEmpty()){
            oldUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

//        userService.updateUser(oldUser, oldUser.getId());
        User newUser = (User) userService.mapToUser(oldUser);
        userService.save(newUser);


        return "redirect:/home";
    }

    @GetMapping(path = {"/rents"})
    public String listRents(Model model, Principal principal) {

        User user = (User) userService.loadUserByUsername(principal.getName());

        List<Rent> rents = rentRepository.findByUser(user);
        model.addAttribute("rents", rents);
        return "all-rents-by-user";
    }
}
