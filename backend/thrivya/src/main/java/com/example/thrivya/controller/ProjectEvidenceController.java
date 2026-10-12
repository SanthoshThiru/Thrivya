package com.example.thrivya.controller;

import com.example.thrivya.dto.request.CreateProjectEvidenceRequest;
import com.example.thrivya.model.ProjectEvidence;
import com.example.thrivya.service.ProjectEvidenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student/projects")
@RequiredArgsConstructor
public class ProjectEvidenceController {

    private final ProjectEvidenceService projectEvidenceService;

    @PostMapping
    public ResponseEntity<ProjectEvidence> createProject(
            Authentication authentication,
            @Valid @RequestBody
            CreateProjectEvidenceRequest request
    ) {
        ProjectEvidence project =
                projectEvidenceService.createProject(
                        authentication.getName(),
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(project);
    }
}