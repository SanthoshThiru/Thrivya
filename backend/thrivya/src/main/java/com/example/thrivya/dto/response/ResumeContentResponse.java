package com.example.thrivya.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeContentResponse {

    private Long draftId;
    private Long resumeId;
    private Integer resumeVersion;
    private String selectedRole;
    private String summary;
    private String skills;
    private String projects;
    private String experience;
    private String evidenceStatus;
    private List<GeneratedEvidenceResponse> evidence;
    private String message;
}