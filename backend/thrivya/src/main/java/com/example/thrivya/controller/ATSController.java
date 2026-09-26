package com.example.thrivya.controller;

import com.example.thrivya.dto.response.ATSHistoryItemResponse;
import com.example.thrivya.dto.response.ATSReportResponse;
import com.example.thrivya.service.ATSService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/student/ats")
@RequiredArgsConstructor
public class ATSController {

    private final ATSService atsService;

    @PostMapping("/generate")
    public ResponseEntity<ATSReportResponse> generateReport(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                atsService.generateReport(
                        authentication.getName()
                )
        );
    }

    @GetMapping("/latest")
    public ResponseEntity<ATSReportResponse> getLatestReport(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                atsService.getLatestReport(
                        authentication.getName()
                )
        );
    }

    @GetMapping("/history")
    public ResponseEntity<List<ATSHistoryItemResponse>> getHistory(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                atsService.getHistory(
                        authentication.getName()
                )
        );
    }
}