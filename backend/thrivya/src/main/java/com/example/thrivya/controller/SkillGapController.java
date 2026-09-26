package com.example.thrivya.controller;

import com.example.thrivya.dto.response.SkillGapHistoryResponse;
import com.example.thrivya.dto.response.SkillGapSummaryResponse;
import com.example.thrivya.service.SkillGapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student/skill-gaps")
@RequiredArgsConstructor
public class SkillGapController {

    private final SkillGapService skillGapService;

    @PostMapping("/calculate")
    public ResponseEntity<SkillGapSummaryResponse> calculate(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                skillGapService.recalculateForCurrentRole(
                        authentication.getName()
                )
        );
    }

    @GetMapping
    public ResponseEntity<SkillGapSummaryResponse> getCurrent(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                skillGapService.getCurrentGaps(
                        authentication.getName()
                )
        );
    }

    @GetMapping("/history")
    public ResponseEntity<SkillGapHistoryResponse> getHistory(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                skillGapService.getHistory(
                        authentication.getName()
                )
        );
    }
}