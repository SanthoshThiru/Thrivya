package com.example.thrivya.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeVersionResponse {

    private Long resumeId;
    private Integer versionNumber;
    private String originalFileName;
    private LocalDateTime uploadedAt;
    private Boolean active;
    private Double atsScore;
    private Long skillCount;
}