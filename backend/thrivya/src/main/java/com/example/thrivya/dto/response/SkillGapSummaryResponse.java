package com.example.thrivya.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillGapSummaryResponse {

    private Long profileId;
    private Long roleId;
    private String roleTitle;
    private Double overallReadinessPercentage;
    private Double overallGapPercentage;
    private List<SkillGapResponse> gaps;
}