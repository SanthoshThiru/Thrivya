package com.example.thrivya.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillGapHistoryItemResponse {

    private Integer analysisVersion;
    private Long resumeId;
    private Integer resumeVersion;
    private String roleTitle;
    private Double averageGapPercentage;
    private Double readinessPercentage;
    private LocalDateTime calculatedAt;
}