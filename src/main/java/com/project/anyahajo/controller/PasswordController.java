package com.project.anyahajo.controller;
//import com.project.anyahajo.model.PasswordResetToken;
import com.project.anyahajo.model.User;
import com.project.anyahajo.service.UserService;
import com.project.anyahajo.auth.EmailSender;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PasswordController {

    @Autowired
    private EmailSender emailSender;

    @Autowired
    private UserService userService;

//    @PostMapping("/forgot-password")
//    public ResponseEntity<?> processForgotPassword(@RequestBody ForgotPasswordRequest request, HttpServletRequest servletRequest) {
//        // ellenőrizze, hogy a felhasználói fiók létezik-e a megadott e-mail cím alapján
//        User user = userService.findByEmail(request.getEmail());
//        if (user == null) {
//            // ha az e-mail cím nem létezik a rendszerben
//            return ResponseEntity.badRequest().body("Az e-mail cím nem található a rendszerben.");
//        }
//
//        // ha a felhasználói fiók létezik, akkor hozzon létre egy jelszó-visszaállítási token-t a felhasználóhoz
//        String token = UUID.randomUUID().toString();
//        userService.createPasswordResetTokenForUser(user, token);
//
//        // elküldi a jelszó-visszaállítási linket tartalmazó email-t a felhasználónak
//        String resetUrl = servletRequest.getScheme() + "://" + servletRequest.getServerName() + ":" + servletRequest.getServerPort() + "/reset-password?token=" + token;
//        String emailBody = "Kattintson a következő linkre a jelszó visszaállításához: " + resetUrl;
//        emailSender.send(user.getEmail(), emailBody);
//
//        // visszatérési érték a siker visszajelzéséhez a felhasználónak
//        return ResponseEntity.ok("E-mailt küldtünk a jelszó-visszaállítási linkkel.");
//    }
//    @GetMapping("/reset-password")
//    public ResponseEntity<?> showResetPasswordForm(@RequestParam("token") String token) {
//        resetPasswordToken resetToken = userService.getPasswordResetToken(token);
//        if (resetToken == null) {
//            return ResponseEntity.badRequest().body("Invalid password reset token.");
//        } else {
//            // Token érvényes, jelszó visszaállító űrlapot jelenít meg
//            return ResponseEntity.ok("Show password reset form.");
//        }
//    }
//}


