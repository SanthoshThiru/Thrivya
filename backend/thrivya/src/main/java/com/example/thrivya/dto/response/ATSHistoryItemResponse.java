package com.example.thrivya.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ATSHistoryItemResponse {

    private Long reportId;

    private Long resumeId;

    private Integer resumeVersion;

    private Long roleId;

    private String roleTitle;

    private Double overallScore;

    private LocalDateTime generatedAt;
}