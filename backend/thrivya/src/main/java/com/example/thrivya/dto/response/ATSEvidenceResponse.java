package com.example.thrivya.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ATSEvidenceResponse {

    private String factorName;

    private String itemName;

    private String status;

    private String evidenceText;

    private String recommendation;
}