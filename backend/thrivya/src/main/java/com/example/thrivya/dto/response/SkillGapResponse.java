package com.example.thrivya.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillGapResponse {

    private Long skillGapId;
    private Long skillId;
    private String skillName;
    private String roleTitle;
    private Long resumeId;
    private Integer resumeVersion;
    private Integer analysisVersion;
    private Integer priorityLevel;
    private String learningStage;
    private Double currentLevel;
    private Double targetLevel;
    private Double gapPercentage;
    private String status;
    private String rationale;
    private String evidenceText;
}