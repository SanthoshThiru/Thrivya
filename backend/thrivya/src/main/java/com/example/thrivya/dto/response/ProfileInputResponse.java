package com.example.thrivya.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileInputResponse {

    private Long profileId;
    private Long resumeId;
    private String inputMode;
    private String fileName;
    private String extractedText;
    private String desiredRole;
    private Long selectedRoleId;
    private String selectedRoleTitle;
    private List<SkillResponse> skills;
    private String message;
}