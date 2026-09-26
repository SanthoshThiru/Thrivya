package com.example.thrivya.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ATSReportResponse {

    private Long reportId;

    private Long resumeId;

    private Integer resumeVersion;

    private Long roleId;

    private String roleTitle;

    private Double overallScore;

    private List<ATSFactorResponse> factors;

    private String summary;
}