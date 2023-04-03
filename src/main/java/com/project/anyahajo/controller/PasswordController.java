package com.project.anyahajo.controller;
import com.project.anyahajo.form.RegistrationForm;;
import com.project.anyahajo.form.ResetForm;
import com.project.anyahajo.model.User;
import com.project.anyahajo.repository.UserRepository;
import com.project.anyahajo.service.UserService;
import com.project.anyahajo.auth.EmailSender;
import com.project.anyahajo.timer.TokenExpiryTask;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Timer;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class PasswordController {

    @NonNull
    private EmailSender emailSender;
    @NonNull
    private UserService userService;
    @NonNull
    private UserRepository userRepository;
    @NonNull
    private final PasswordEncoder passwordEncoder;
    @NonNull
    private final UserRepository appUserRepository;

    @PostMapping("/forgot-password")
    public String processForgotPassword(
            @RequestParam("email") String email
    )
    {
        User user = userService.findUserByUserEmail(email);
        if (user == null) {
            return "success-fail";
        }

        long tokenExpiration = System.currentTimeMillis() + (30 * 60 * 1000);
        String token = UUID.randomUUID().toString();
        user.setResetPasswordToken(token);
        user.setTokenExpiration(new Timestamp(tokenExpiration).toLocalDateTime());
        userRepository.save(user);

        Timer timer = new Timer();
        timer.schedule(new TokenExpiryTask(user, userRepository), new Date(tokenExpiration));

        String resetUrl = "http://localhost:8080/forgot-password/token=" + token;
        String emailBody = "Kattintson a következő linkre a jelszó visszaállításához: " + resetUrl +
                "\nA link 30 perc mulva lejar";
        try {
            emailSender.send(user.getEmail(), emailBody, "Jelszó változtatás!");
            System.out.println(email + "  " + emailBody);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            System.out.println(emailBody);
        }
        return "success-reset";
    }
    @GetMapping("/forgot-password/token={bob}")
        public String newerPassword(@PathVariable ("bob")String token, Model model){
            User user = userService.findpasswordtoken(token);
        if (user == null){
            return "token-expired";
        }
            model.addAttribute("user", user);
            model.addAttribute("newUser",new ResetForm());
            return "new-password";
       }
    @PostMapping("/forgot-password/token={bob}")
    public String rebaseForgotPassword(
            @ModelAttribute("newUser")
            @Validated
            ResetForm resetForm,
            BindingResult bindingResult,
            @PathVariable ("bob")String token
    ){
        User user = userService.findpasswordtoken(token);
        if (user == null){
            return "token-expired";
        }

        if (bindingResult.hasErrors()){
            return "new-password";
        }
        if (!resetForm.getPassword().equals(resetForm.getPasswordCheck())) {
            bindingResult.rejectValue("passwordCheck", "registrationForm.passwordCheck",
                    "A jelszavak nem egyeznek");
            return "new-password";
        }

        user.setPassword(passwordEncoder.encode(resetForm.getPassword()));
        user.setResetPasswordToken(null);
        user.setTokenExpiration(null);

        appUserRepository.save(user);

        return "redirect:/login";
    }

        @GetMapping("/forgot-password")
    public String showResetPasswordForm() {
            return "forgot-password";
        }

}



