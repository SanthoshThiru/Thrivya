package com.example.thrivya.controller;

import com.example.thrivya.dto.response.ResourceResponse;
import com.example.thrivya.model.RecommendationType;
import com.example.thrivya.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping
    public ResponseEntity<List<ResourceResponse>>
    getRecommendations(
            Authentication authentication,
            @RequestParam String skill,
            @RequestParam RecommendationType type
    ) {
        return ResponseEntity.ok(
                recommendationService.getRecommendations(
                        skill,
                        type
                )
        );
    }
}