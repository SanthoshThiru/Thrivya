package com.example.thrivya.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard(
            Authentication authentication
    ) {
        return Map.of(
                "role", "STUDENT",
                "title", "Student Dashboard",
                "message", "Student dashboard accessed",
                "email", authentication.getName()
        );
    }
}