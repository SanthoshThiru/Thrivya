package com.example.thrivya.service;

import com.example.thrivya.dto.request.CreateProjectEvidenceRequest;
import com.example.thrivya.model.*;
import com.example.thrivya.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectEvidenceService {

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final ProjectEvidenceRepository projectEvidenceRepository;
    private final ProjectEvidenceSkillRepository projectEvidenceSkillRepository;

    @Transactional
    public ProjectEvidence createProject(
            String email,
            CreateProjectEvidenceRequest request
    ) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Authenticated user not found"
                        ));

        ProjectEvidence project =
                ProjectEvidence.builder()
                        .user(user)
                        .projectName(
                                request.getProjectName().trim()
                        )
                        .projectDescription(
                                request.getProjectDescription().trim()
                        )
                        .studentAction(
                                request.getStudentAction().trim()
                        )
                        .resultText(
                                cleanOptional(
                                        request.getResultText()
                                )
                        )
                        .githubUrl(
                                cleanOptional(
                                        request.getGithubUrl()
                                )
                        )
                        .liveUrl(
                                cleanOptional(
                                        request.getLiveUrl()
                                )
                        )
                        .verificationStatus(
                                "USER_CONFIRMED"
                        )
                        .build();

        ProjectEvidence savedProject =
                projectEvidenceRepository.save(project);

        if (request.getSkillNames() != null) {
            for (String skillName : request.getSkillNames()) {
                if (skillName == null
                        || skillName.isBlank()) {
                    continue;
                }

                Skill skill =
                        skillRepository
                                .findByCanonicalNameIgnoreCase(
                                        skillName.trim()
                                )
                                .orElseGet(() ->
                                        skillRepository.save(
                                                Skill.builder()
                                                        .canonicalName(
                                                                skillName.trim()
                                                        )
                                                        .build()
                                        )
                                );

                projectEvidenceSkillRepository.save(
                        ProjectEvidenceSkill.builder()
                                .projectEvidence(
                                        savedProject
                                )
                                .skill(skill)
                                .build()
                );
            }
        }

        return savedProject;
    }

    private String cleanOptional(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}