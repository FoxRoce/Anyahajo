package com.project.anyahajo.timer;

import com.project.anyahajo.model.User;
import com.project.anyahajo.repository.UserRepository;

import java.util.TimerTask;

public class TokenExpiryTask extends TimerTask {
    private User user;
    private UserRepository userRepository;

    public TokenExpiryTask(User user, UserRepository userRepository) {
        this.user = user;
        this.userRepository = userRepository;
    }

    @Override
    public void run() {
        user.setResetPasswordToken(null);
        user.setTokenExpiration(null);
        userRepository.save(user);
    }
}