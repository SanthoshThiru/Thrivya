package com.example.thrivya.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ATSFactorResponse {

    private String factorName;

    private Double score;

    private Double weight;

    private List<ATSEvidenceResponse> evidence;
}