package com.project.anyahajo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AnyahajoController {

    @GetMapping(path = {"", "/", "/home"})
    public String getHomePage() {
        return "home";
    }

}
