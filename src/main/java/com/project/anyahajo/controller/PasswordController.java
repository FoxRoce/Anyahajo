package com.project.anyahajo.controller;
import com.project.anyahajo.model.User;
import com.project.anyahajo.repository.UserRepository;
import com.project.anyahajo.service.UserService;
import com.project.anyahajo.auth.EmailSender;
import com.sun.mail.util.MailConnectException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    @PostMapping("/reset-password")
    public String processForgotPassword(
            @RequestParam("email") String email
    ) {
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
        String resetUrl = "http://localhost:8080/reset-password?token=" + token;
        String emailBody = "Kattintson a következő linkre a jelszó visszaállításához: " + resetUrl;
        try {
            emailSender.send(user.getEmail(), emailBody);
            System.out.println(email + "  " + emailBody);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
       }
//        @GetMapping"(http://localhost:8080/reset-password?token={token}")
//        Model model;
//        public String newerPassword(@PathVariable("token")String token){
//            model.addAttribute("token", showResetPasswordForm());
//            return
//        }

        // visszatérési érték a siker visszajelzéséhez a felhasználónak
        return "success-reset";
    }
    @GetMapping("/reset-password")
    public String showResetPasswordForm() {
            return "forgot-password";
        }
    }



