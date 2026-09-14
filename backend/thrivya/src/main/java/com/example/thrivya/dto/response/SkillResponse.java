package com.example.thrivya.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillResponse {

    private String name;
    private String source;
    private Double confidence;
    private String evidenceText;
}

