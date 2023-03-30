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
    @PostMapping("/reset-password")
    public String processResetPassword(
            @RequestParam("token") String token,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model
    ) {
        // ellenőrizzük, hogy a két jelszó megegyezik-e
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "A megadott jelszavak nem egyeznek meg.");
            return "reset-password";
        }

        // ellenőrizzük, hogy a token érvényes-e és lejárat nélküli
        User user = userService.findUserByResetPasswordToken(token);
        if (user == null) {
            model.addAttribute("error", "A jelszó-visszaállítási token érvénytelen vagy lejárt.");
            return "reset-password";
        }

        // ha minden ellenőrzés sikeres volt, akkor beállítjuk az új jelszót a felhasználóhoz és töröljük a token-t
        userService.changeUserPassword(user, password);
        user.setResetPasswordToken(null);
        userRepository.save(user);

        // visszairányítjuk a felhasználót a bejelentkezési oldalra
        return "redirect:/login";
    }
    @GetMapping("/new-password")
    public String showResetPasswordForm(
            @RequestParam("token") String token,
            Model model
    ) {
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


//        @GetMapping("/forgot-password")
//    public String showResetPasswordForm() {
//            return "forgot-password";
//        }

}



