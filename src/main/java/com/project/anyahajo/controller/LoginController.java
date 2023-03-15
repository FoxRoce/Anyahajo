package com.project.anyahajo.controller;

import com.project.anyahajo.model.User;
import com.project.anyahajo.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    private AppUserRepository userRepository;

    @GetMapping("/login_page")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new User());
        return "login_page";
    }

    @PostMapping("/login_page")
    public String login(@ModelAttribute("user") User user, Model model) {
        Optional<User> dbUser = userRepository.findByEmail(user.getEmail());
        if (dbUser.isPresent() && dbUser.get().getPassword().equals(user.getPassword())) {
            return "redirect:/home"; //redirect to home page if login successful
        } else {
            model.addAttribute("error", "Hibás email vagy jelszó!");
            return "login_page";
        }
    }
}

