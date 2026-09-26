package com.example.thrivya.controller;

import com.example.thrivya.dto.request.ManualSkillsRequest;
import com.example.thrivya.dto.response.ProfileInputResponse;
import com.example.thrivya.dto.response.ResumeHistoryResponse;
import com.example.thrivya.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/student/profile")
@RequiredArgsConstructor
public class StudentProfileController {

    private final ProfileService profileService;

    @PostMapping("/manual-skills")
    public ResponseEntity<ProfileInputResponse> saveManualSkills(
            Authentication authentication,
            @Valid @RequestBody ManualSkillsRequest request
    ) {
        ProfileInputResponse response =
                profileService.saveManualSkills(
                        authentication.getName(),
                        request.getSkillsText()
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping(
            value = "/resume",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ProfileInputResponse> uploadResume(
            Authentication authentication,
            @RequestParam("file") MultipartFile file
    ) {
        ProfileInputResponse response =
                profileService.saveResume(
                        authentication.getName(),
                        file
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<ProfileInputResponse> getProfile(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                profileService.getCurrentProfile(
                        authentication.getName()
                )
        );
    }

    @GetMapping("/resume-history")
    public ResponseEntity<ResumeHistoryResponse> getResumeHistory(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                profileService.getResumeHistory(
                        authentication.getName()
                )
        );
    }
}