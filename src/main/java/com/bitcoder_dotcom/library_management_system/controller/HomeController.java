package com.bitcoder_dotcom.library_management_system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("lms/")
    public String home() {
        return "home";
    }
}
