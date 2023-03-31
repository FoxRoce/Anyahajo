package com.project.anyahajo.controller;
import com.project.anyahajo.form.RegistrationForm;
import com.project.anyahajo.model.Name;
import com.project.anyahajo.model.Role;
import com.project.anyahajo.model.User;
import com.project.anyahajo.repository.UserRepository;
import com.project.anyahajo.service.UserService;
import com.project.anyahajo.auth.EmailSender;
import com.project.anyahajo.service.impl.UserServiceImpl;
import com.sun.mail.util.MailConnectException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.ConnectException;
import java.util.InvalidPropertiesFormatException;
import java.util.UUID;

@Controller
public class PasswordController {

    @Autowired
    private EmailSender emailSender;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserServiceImpl userServiceImplement;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository appUserRepository;
    public PasswordController(
            PasswordEncoder passwordEncoder,
            UserRepository appUserRepository
    ) {
        this.passwordEncoder = passwordEncoder;
        this.appUserRepository = appUserRepository;
    }


    @PostMapping("/forgot-password")
    public String processForgotPassword(
            @RequestParam("email") String email
    )
    {
        // ellenőrizze, hogy a felhasználói fiók létezik-e a megadott e-mail cím alapján
        User user = userService.findUserByUserEmail(email);
        if (user == null) {
            // ha az e-mail cím nem létezik a rendszerben
            return "success-reset";
        }

        // ha a felhasználói fiók létezik, akkor hozzon létre egy jelszó-visszaállítási token-t a felhasználóhoz
        String token = UUID.randomUUID().toString();
//        userService.createPasswordResetTokenForUser(user, token);
        user.setResetPasswordToken(token);
        userRepository.save(user);

        // elküldi a jelszó-visszaállítási linket tartalmazó email-t a felhasználónak
        String resetUrl = "http://localhost:8080/forgot-password/token=" + token;
        String emailBody = "Kattintson a következő linkre a jelszó visszaállításához: " + resetUrl;
        try {
            emailSender.send(user.getEmail(), emailBody);
            System.out.println(email + "  " + emailBody);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
       }
        // visszatérési érték a siker visszajelzéséhez a felhasználónak
        return "success-reset";
    }
    @GetMapping("/forgot-password/token={token}")
        public String newerPassword(@PathVariable ("token")String token, Model model){
            User user = userServiceImplement.findpasswordtoken(token);
            model.addAttribute("user", user);
//            model.addAttribute("token", showResetPasswordForm());
            model.addAttribute("newUser",new RegistrationForm());
            return "new-password";
       }
    @PostMapping("/forgot-password/token={token}")
    public String rebaseForgotPassword(
            @ModelAttribute("newUser")
            @Validated
            RegistrationForm registrationForm,
            BindingResult bindingResult,
            @PathVariable ("token")String token
    ){
        if (bindingResult.hasErrors()){
            return "new-password";
        }
        if (!registrationForm.getPassword().equals(registrationForm.getPasswordCheck())) {
            bindingResult.rejectValue("passwordCheck", "registrationForm.passwordCheck",
                    "A jelszavak nem egyeznek");
            return "new-password";
        }

//        String encodePw = passwordEncoder.encode(password);
        User user = userService.findpasswordtoken(token);
        user.setPassword(passwordEncoder.encode(registrationForm.getPassword()));

        appUserRepository.save(user);

        return "redirect:/login";
    }


//    @GetMapping("/new-password")
//    public String showResetPasswordForm(
//            @RequestParam("token") String token,
//            Model model
//    ) {
//        // ellenőrizzük, hogy a token érvényes-e és lejáratnál.
////        passwordResetToken = userService.findUserByResetPasswordToken(token);
////        if (passwordResetToken == null) {
//            // Ha a token érvénytelen vagy lejárt, akkor jelenítse meg a megfelelő hibaüzenetet
//            model.addAttribute("errorMessage", "A jelszó-visszaállítási link érvénytelen vagy lejárt.");
//            return "reset-password-error";
//        }
//
//        // Tegyük lehetővé a jelszó visszaállítását a felhasználó számára
//        model.addAttribute("token", token);
//        return "new-password";
//    }


        @GetMapping("/forgot-password")
    public String showResetPasswordForm() {
            return "forgot-password";
        }

}



