package com.example.thrivya.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/placement")
public class PlacementCellController {

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard(
            Authentication authentication
    ) {
        return Map.of(
                "role", "PLACEMENT_CELL",
                "title", "Placement Cell Dashboard",
                "message", "Placement cell dashboard accessed",
                "email", authentication.getName()
        );
    }
}