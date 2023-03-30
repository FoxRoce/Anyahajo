package com.project.anyahajo.controller;

import com.project.anyahajo.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class LoginController {

    /*@Autowired
    private AppUserRepository userRepository;*/

    @GetMapping("/login")
    public String showLoginForm(Model model, Principal principal) {
        if (principal != null) {
            return "redirect:/home";
        }
        model.addAttribute("user", new User());
        return "login";
    }

    /*@PostMapping("/login")
    public String login(@ModelAttribute("user") User user, Model model) {
        Optional<User> dbUser = userRepository.findByEmail(user.getEmail());
        if (dbUser.isPresent() && dbUser.get().getPassword().equals(user.getPassword())) {
            return "redirect:/home";
        } else {
            model.addAttribute("error", "Hibás email vagy jelszó!");
            return "login";
        }
    }*/
}

