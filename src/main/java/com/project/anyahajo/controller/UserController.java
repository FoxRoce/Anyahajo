package com.project.anyahajo.controller;

import com.project.anyahajo.form.UserForm;
import com.project.anyahajo.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = {"/admin/users"})
    public String listItems(Model model) {
        List<UserForm> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "all-users";
    }

    @PostMapping("user-admin")
    public String UserIsAdmin(@RequestParam("user_id") Long id,
                           @RequestParam(value = "admin", required = false) Boolean admin) {
        admin = admin != null;
        userService.updateIsadmin(admin, id);

        return "redirect:/home";
    }
}
