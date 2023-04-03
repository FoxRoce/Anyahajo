package com.project.anyahajo.controller;

import com.project.anyahajo.auth.EmailSender;
import com.project.anyahajo.form.RegistrationForm;
import com.project.anyahajo.model.Name;
import com.project.anyahajo.model.Role;
import com.project.anyahajo.model.User;
import com.project.anyahajo.repository.UserRepository;
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
public class AuthController {

    public AuthController(
            PasswordEncoder passwordEncoder,
            UserRepository appUserRepository,
            EmailSender emailSender
    ) {
        this.passwordEncoder = passwordEncoder;
        this.appUserRepository = appUserRepository;
        this.emailSender = emailSender;
    }

    private final PasswordEncoder passwordEncoder;
    private final UserRepository appUserRepository;
    private final EmailSender emailSender;

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
        appUserRepository.save(user);

        try {
            emailSender.send(user.getEmail(),emailBody,"Fiók aktiválás");
        } catch (Exception e){
            System.out.println(emailBody);

        }

        return "redirect:/home";
    }

    @GetMapping("/enable-user/url={bob}")
    public String enableUser(
            @PathVariable ("bob")String bob
    ){
        User user = appUserRepository.findByEnableUrl(bob);
        if (user == null){
            return "token-expired";
        }
        user.setEnabled(true);
        user.setEnableUrl(null);

        appUserRepository.save(user);

        return "success-active";
    }
}
