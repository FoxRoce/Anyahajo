package com.project.anyahajo.controller;

import com.project.anyahajo.auth.EmailSender;
import com.project.anyahajo.form.RegistrationForm;
import com.project.anyahajo.model.Name;
import com.project.anyahajo.model.Role;
import com.project.anyahajo.model.User;
import com.project.anyahajo.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class AuthController {

    @NonNull
    private final PasswordEncoder passwordEncoder;
    @NonNull
    private final UserService userService;
    @NonNull
    private final EmailSender emailSender;


    @GetMapping(path = {"", "/", "/home", "/#menu"})
    public String getHomePage() {
        return "home";
    }

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
        if (userService.findByEmail(registrationForm.getEmail()).isPresent()) {
            bindingResult.rejectValue("email", "registrationForm.email",
                    "Már létező email cím");
            return "register";
        }

        User user = new User();
        Name name = new Name();
        name.setLastName(registrationForm.getLastName());
        name.setFirstName(registrationForm.getFirstName());
        user.setName(name);
        user.setEmail(registrationForm.getEmail());
        user.setPhoneNumber(registrationForm.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(registrationForm.getPassword()));
        user.setLocked(false);

        user.setRole(Role.USER);

        String url = UUID.randomUUID().toString();
        String enableUrl = "http://localhost:8080/enable-user/url=" + url;

        String emailBody = "Kedves " + user.getName() + "!\n\n" +
                "Kérjük az alábbi linken aktiválja a fiókját: \n" +
                enableUrl  +
                "\n\nÜdvözlettel, Anyahajó";

        user.setEnableUrl(url);
        userService.save(user);

        try {
            emailSender.send(user.getEmail(),emailBody,"Fiók aktiválás");
        } catch (Exception e){
            System.out.println(emailBody);

        }

        return "redirect:/#menu";
    }

    @GetMapping("/enable-user/url={bob}")
    public String enableUser(
            @PathVariable ("bob")String bob
    ){
        User user = userService.findByEnableUrl(bob);
        if (user == null){
            return "token-expired";
        }
        user.setEnabled(true);
        user.setEnableUrl(null);

        userService.save(user);

        return "success-active";
    }
}
