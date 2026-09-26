package com.example.thrivya.controller;

import com.example.thrivya.dto.request.SkillProgressUpdateRequest;
import com.example.thrivya.service.SkillProgressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final SkillProgressService skillProgressService;

    @PostMapping
    public ResponseEntity<?> updateProgress(
            Authentication authentication,
            @Valid @RequestBody
            SkillProgressUpdateRequest request
    ) {
        skillProgressService.updateProgress(
                authentication.getName(),
                request
        );

        return ResponseEntity.ok(
                java.util.Map.of(
                        "message",
                        "Skill progress updated successfully"
                )
        );
    }
}