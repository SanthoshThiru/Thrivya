package com.example.thrivya.service;

import com.example.thrivya.dto.response.GeneratedEvidenceResponse;
import com.example.thrivya.model.ProfileSkill;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EvidenceGuardrailService {

    public List<GeneratedEvidenceResponse> verifySkills(
            List<ProfileSkill> profileSkills
    ) {
        List<GeneratedEvidenceResponse> results =
                new ArrayList<>();

        for (ProfileSkill profileSkill : profileSkills) {
            String status = determineStatus(profileSkill);

            boolean requiresConfirmation =
                    !"VERIFIED".equals(status);

            results.add(
                    GeneratedEvidenceResponse.builder()
                            .claim(
                                    profileSkill.getSkill()
                                            .getCanonicalName()
                            )
                            .status(status)
                            .source(
                                    profileSkill.getSource()
                            )
                            .evidenceText(
                                    profileSkill.getEvidenceText()
                            )
                            .requiresUserConfirmation(
                                    requiresConfirmation
                            )
                            .build()
            );
        }

        return results;
    }

    public boolean isSafeForAutomaticResumeUse(
            GeneratedEvidenceResponse evidence
    ) {
        return "VERIFIED".equals(
                evidence.getStatus()
        );
    }

    public boolean isSafeForDraftWithReview(
            GeneratedEvidenceResponse evidence
    ) {
        return "VERIFIED".equals(
                evidence.getStatus()
        ) || "USER_CONFIRMED".equals(
                evidence.getStatus()
        );
    }

    private String determineStatus(
            ProfileSkill profileSkill
    ) {
        String source = profileSkill.getSource();

        String evidenceText =
                profileSkill.getEvidenceText();

        Double confidence =
                profileSkill.getConfidence();

        if (evidenceText == null
                || evidenceText.isBlank()) {
            return "REQUIRES_CONFIRMATION";
        }

        if ("RESUME_TEXT".equals(source)
                || "PROJECT".equals(source)) {
            return "VERIFIED";
        }

        if ("MANUAL_PROGRESS".equals(source)
                && confidence != null
                && confidence >= 0.60) {
            return "USER_CONFIRMED";
        }

        if ("MANUAL_INPUT".equals(source)
                && confidence != null
                && confidence >= 0.80) {
            return "USER_CONFIRMED";
        }

        return "INFERRED";
    }
}