package com.example.thrivya.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeneratedEvidenceResponse {

    private String claim;

    private String status;

    private String source;

    private String evidenceText;

    private Boolean requiresUserConfirmation;
}