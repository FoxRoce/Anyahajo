package com.project.anyahajo.controller;

import com.project.anyahajo.form.UserForm;;
import com.project.anyahajo.model.Role;
import com.project.anyahajo.model.User;
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

@Controller
@RequiredArgsConstructor
public class UserController {

    @NonNull
    private UserService userService;
    @NonNull
    private PasswordEncoder passwordEncoder;



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
    public String editUserForm(
            Principal principal,
            Model model
    ) {
        User userGet = (User) userService.loadUserByUsername(principal.getName());
        UserForm user = userService.mapToUserForm(userGet);
        model.addAttribute("user", user);
        return "user-edit";
    }

    @PostMapping("/user/edit")
    public String updateClub(
            @Validated @ModelAttribute("user") UserForm user,
            BindingResult bindingResult,
            Model model,
            Principal principal
    ) {
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

        User newUser = (User) userService.mapToUser(oldUser);
        userService.save(newUser);


        return "redirect:/#menu";
    }

}
