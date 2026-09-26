package com.example.thrivya.service;

import com.example.thrivya.dto.request.SkillProgressUpdateRequest;
import com.example.thrivya.model.*;
import com.example.thrivya.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SkillProgressService {

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final ProfileSkillRepository profileSkillRepository;
    private final CareerProfileRepository profileRepository;
    private final SkillProgressUpdateRepository progressRepository;
    private final SkillGapService skillGapService;

    @Transactional
    public void updateProgress(
            String email,
            SkillProgressUpdateRequest request
    ) {
        User user =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Authenticated user not found"
                                ));

        Skill skill =
                skillRepository
                        .findByCanonicalNameIgnoreCase(
                                request.getSkillName()
                        )
                        .orElseGet(() ->
                                skillRepository.save(
                                        Skill.builder()
                                                .canonicalName(
                                                        request.getSkillName()
                                                )
                                                .build()
                                )
                        );

        double level =
                request.getReportedLevel() == null
                        ? 0.5
                        : Math.min(
                        1.0,
                        Math.max(
                                0.0,
                                request.getReportedLevel()
                        )
                );

        SkillProgressUpdate progress =
                SkillProgressUpdate.builder()
                        .user(user)
                        .skill(skill)
                        .updateText(
                                request.getUpdateText()
                        )
                        .reportedLevel(level)
                        .source("MANUAL_UPDATE")
                        .build();

        progressRepository.save(progress);

        CareerProfile profile =
                profileRepository.findByUserId(user.getId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Career profile not found"
                                ));

        ProfileSkill profileSkill =
                profileSkillRepository
                        .findAllByProfileId(profile.getId())
                        .stream()
                        .filter(item ->
                                item.getSkill()
                                        .getId()
                                        .equals(skill.getId())
                        )
                        .findFirst()
                        .orElseGet(() ->
                                ProfileSkill.builder()
                                        .profile(profile)
                                        .skill(skill)
                                        .source(
                                                "MANUAL_PROGRESS"
                                        )
                                        .build()
                        );

        profileSkill.setConfidence(level);
        profileSkill.setSource("MANUAL_PROGRESS");
        profileSkill.setEvidenceText(
                request.getUpdateText()
        );

        profileSkillRepository.save(profileSkill);

        if (profile.getSelectedRole() != null) {
            skillGapService.recalculateForCurrentRole(
                    email
            );
        }
    }
}