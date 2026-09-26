package com.example.thrivya.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeHistoryResponse {

    private Long activeResumeId;
    private Integer activeVersionNumber;
    private List<ResumeVersionResponse> versions;
}