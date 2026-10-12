package com.example.thrivya.controller;

import com.example.thrivya.dto.response.ResumeContentResponse;
import com.example.thrivya.service.ResumeContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student/resume-content")
@RequiredArgsConstructor
public class ResumeContentController {

    private final ResumeContentService resumeContentService;

    @PostMapping("/generate")
    public ResponseEntity<ResumeContentResponse>
    generate(Authentication authentication) {
        return ResponseEntity.ok(
                resumeContentService.generate(
                        authentication.getName()
                )
        );
    }
}