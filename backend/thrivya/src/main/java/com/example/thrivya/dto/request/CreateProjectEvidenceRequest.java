package com.example.thrivya.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProjectEvidenceRequest {

    @NotBlank(message = "Project name is required")
    private String projectName;

    @NotBlank(message = "Project description is required")
    private String projectDescription;

    @NotBlank(message = "Your contribution is required")
    private String studentAction;

    private String resultText;

    private String githubUrl;

    private String liveUrl;

    private List<String> skillNames;
}