package com.example.thrivya.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @GetMapping("/me")
    public Map<String, Object> currentUser(
            Authentication authentication
    ) {
        return Map.of(
                "message", "Authenticated successfully",
                "email", authentication.getName(),
                "authorities", authentication.getAuthorities()
        );
    }
}