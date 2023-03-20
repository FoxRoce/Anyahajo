package com.project.anyahajo.controller;

import com.project.anyahajo.form.RegistrationForm;
import com.project.anyahajo.model.Name;
import com.project.anyahajo.model.User;
import com.project.anyahajo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    public AuthController(
            PasswordEncoder passwordEncoder,
            UserRepository appUserRepository
    ) {
        this.passwordEncoder = passwordEncoder;
        this.appUserRepository = appUserRepository;
    }

    private final PasswordEncoder passwordEncoder;
    private final UserRepository appUserRepository;

    @GetMapping("/register")
    public String newUser(
            Model model
    ){
        RegistrationForm registrationForm = new RegistrationForm();
        model.addAttribute("newUser", registrationForm);
        return "register";
    }

    @PostMapping("/register")
    public String saveUser(
            @ModelAttribute("newUser")
            @Validated
            RegistrationForm registrationForm,
            BindingResult bindingResult
    ){
        if (bindingResult.hasErrors()){
            return "register";
        }
        if (!registrationForm.getPassword().equals(registrationForm.getPasswordCheck())) {
            bindingResult.rejectValue("passwordCheck", "registrationForm.passwordCheck",
                    "A jelszavak nem egyeznek");
            return "register";
        }
        if (appUserRepository.findByEmail(registrationForm.getEmail()).isPresent()) {
            bindingResult.rejectValue("email", "registrationForm.email",
                    "Már létező email cím");
            return "register";
        }

//        String encodePw = passwordEncoder.encode(password);
        User user = new User();
        Name name = new Name();
        name.setLastName(registrationForm.getLastName());
        name.setFirstName(registrationForm.getFirstName());
        user.setName(name);
        user.setEmail(registrationForm.getEmail());
        user.setPassword(passwordEncoder.encode(registrationForm.getPassword()));
        user.setEnabled(true);
        user.setLocked(false);

        appUserRepository.save(user);

        return "redirect:/home";
    }
}
