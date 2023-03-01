package com.project.anyahajo.controller;

import com.project.anyahajo.model.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class UserController {

    @NonNull
    private UserRepository userRepository;
}
