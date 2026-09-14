package com.example.thrivya.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManualSkillsRequest {

    @NotBlank(message = "Skills are required")
    private String skillsText;
}