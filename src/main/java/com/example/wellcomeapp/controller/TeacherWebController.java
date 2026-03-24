package com.example.wellcomeapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/web/teacher")
public class TeacherWebController {

    @GetMapping("/login")
    public String loginPage() {
        return "web/login";
    }

    @GetMapping("/dashboard")
    public String dashboardPage() {
        return "web/dashboard";
    }
}
