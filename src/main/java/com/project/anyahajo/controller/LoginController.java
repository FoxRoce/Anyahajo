package com.project.anyahajo.controller;

import com.project.anyahajo.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String showLoginForm(Model model, Principal principal) {
        if (principal != null) {
            return "redirect:/home";
        }
        model.addAttribute("user", new User());
        return "login";
    }

}

