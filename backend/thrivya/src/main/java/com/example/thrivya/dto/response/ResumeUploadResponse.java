package com.example.thrivya.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeUploadResponse {

    private Long resumeId;
    private String originalFileName;
    private String contentType;
    private Long fileSize;
    private LocalDateTime uploadedAt;
    private String message;
}