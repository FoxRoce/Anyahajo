package com.project.anyahajo.timer;

import com.project.anyahajo.model.User;
import com.project.anyahajo.repository.UserRepository;
import com.project.anyahajo.service.UserService;

import java.util.TimerTask;

public class TokenExpiryTask extends TimerTask {
    private User user;
    private UserService userService;

    public TokenExpiryTask(User user, UserService userService) {
        this.user = user;
        this.userService = userService;
    }

    @Override
    public void run() {
        user.setResetPasswordToken(null);
        user.setTokenExpiration(null);
        userService.save(user);
    }
}