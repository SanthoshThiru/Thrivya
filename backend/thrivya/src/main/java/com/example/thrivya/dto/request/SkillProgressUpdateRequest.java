package com.example.thrivya.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillProgressUpdateRequest {

    @NotBlank(message = "Skill name is required")
    private String skillName;

    @NotBlank(message = "Update text is required")
    private String updateText;

    private Double reportedLevel;
}