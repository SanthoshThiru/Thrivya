package com.example.thrivya.service;

import com.example.thrivya.dto.response.GeneratedEvidenceResponse;
import com.example.thrivya.dto.response.ResumeContentResponse;
import com.example.thrivya.model.CareerProfile;
import com.example.thrivya.model.ProfileSkill;
import com.example.thrivya.model.ProjectEvidence;
import com.example.thrivya.model.Resume;
import com.example.thrivya.model.ResumeContentDraft;
import com.example.thrivya.model.User;
import com.example.thrivya.repository.CareerProfileRepository;
import com.example.thrivya.repository.ProfileSkillRepository;
import com.example.thrivya.repository.ProjectEvidenceRepository;
import com.example.thrivya.repository.ResumeContentDraftRepository;
import com.example.thrivya.repository.ResumeRepository;
import com.example.thrivya.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResumeContentService {

    private final UserRepository userRepository;
    private final CareerProfileRepository profileRepository;
    private final ResumeRepository resumeRepository;
    private final ProfileSkillRepository profileSkillRepository;
    private final ProjectEvidenceRepository projectEvidenceRepository;
    private final ResumeContentDraftRepository draftRepository;
    private final EvidenceGuardrailService guardrailService;

    @Transactional
    public ResumeContentResponse generate(String email) {
        User user = getUser(email);

        CareerProfile profile = getProfile(user);

        Resume activeResume = getActiveResume(user);

        List<ProfileSkill> profileSkills =
                profileSkillRepository.findAllByProfileId(
                        profile.getId()
                );

        List<GeneratedEvidenceResponse> allEvidence =
                guardrailService.verifySkills(profileSkills);

        List<GeneratedEvidenceResponse> verifiedEvidence =
                allEvidence.stream()
                        .filter(
                                guardrailService::isSafeForAutomaticResumeUse
                        )
                        .toList();

        List<GeneratedEvidenceResponse> draftEvidence =
                allEvidence.stream()
                        .filter(
                                guardrailService::isSafeForDraftWithReview
                        )
                        .toList();

        List<ProjectEvidence> projects =
                projectEvidenceRepository
                        .findAllByUserIdOrderByCreatedAtDesc(
                                user.getId()
                        );

        String summaryText = createSummary(
                profile,
                verifiedEvidence
        );

        String skillsText = createSkillsSection(
                draftEvidence
        );

        String projectsText = projects.isEmpty()
                ? createEvidenceBasedBullets(draftEvidence)
                : createProjectBullets(projects);

        String experienceText = createExperienceGuidance(
                verifiedEvidence,
                projects
        );

        String evidenceStatus = calculateEvidenceStatus(
                allEvidence,
                verifiedEvidence,
                draftEvidence,
                projects
        );

        ResumeContentDraft draft =
                ResumeContentDraft.builder()
                        .user(user)
                        .resume(activeResume)
                        .role(profile.getSelectedRole())
                        .summaryText(summaryText)
                        .skillsText(skillsText)
                        .projectsText(projectsText)
                        .experienceText(experienceText)
                        .evidenceStatus(evidenceStatus)
                        .build();

        ResumeContentDraft savedDraft =
                draftRepository.save(draft);

        return ResumeContentResponse.builder()
                .draftId(savedDraft.getId())
                .resumeId(activeResume.getId())
                .resumeVersion(
                        activeResume.getVersionNumber()
                )
                .selectedRole(
                        profile.getSelectedRole() == null
                                ? null
                                : profile.getSelectedRole()
                                .getTitle()
                )
                .summary(summaryText)
                .skills(skillsText)
                .projects(projectsText)
                .experience(experienceText)
                .evidenceStatus(evidenceStatus)
                .evidence(allEvidence)
                .message(
                        "Evidence-aware resume content generated successfully. "
                                + "Review all generated text before adding it to your resume."
                )
                .build();
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Authenticated user not found"
                        )
                );
    }

    private CareerProfile getProfile(User user) {
        return profileRepository.findByUserId(user.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Career profile not found"
                        )
                );
    }

    private Resume getActiveResume(User user) {
        return resumeRepository
                .findFirstByUserIdAndActiveTrueOrderByVersionNumberDesc(
                        user.getId()
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Please upload a resume before generating resume content"
                        )
                );
    }

    private String createSkillsSection(
            List<GeneratedEvidenceResponse> evidence
    ) {
        String skills = evidence.stream()
                .map(GeneratedEvidenceResponse::getClaim)
                .filter(skill ->
                        skill != null
                                && !skill.isBlank()
                )
                .distinct()
                .collect(Collectors.joining(", "));

        if (skills.isBlank()) {
            return "No evidence-supported skills are available yet.";
        }

        return skills;
    }

    private String createSummary(
            CareerProfile profile,
            List<GeneratedEvidenceResponse> verifiedEvidence
    ) {
        String roleTitle =
                profile.getSelectedRole() == null
                        ? "technology roles"
                        : profile.getSelectedRole()
                        .getTitle();

        List<String> verifiedSkills =
                verifiedEvidence.stream()
                        .filter(item ->
                                "VERIFIED".equals(
                                        item.getStatus()
                                )
                        )
                        .map(
                                GeneratedEvidenceResponse::getClaim
                        )
                        .filter(skill ->
                                skill != null
                                        && !skill.isBlank()
                        )
                        .distinct()
                        .limit(5)
                        .toList();

        if (verifiedSkills.isEmpty()) {
            return "Technology student preparing for "
                    + roleTitle
                    + " opportunities through structured learning "
                    + "and evidence-based project development.";
        }

        return "Technology student preparing for "
                + roleTitle
                + " opportunities, with resume-supported exposure to "
                + String.join(", ", verifiedSkills)
                + ".";
    }

    private String createEvidenceBasedBullets(
            List<GeneratedEvidenceResponse> evidence
    ) {
        List<String> bullets = evidence.stream()
                .filter(
                        guardrailService::isSafeForDraftWithReview
                )
                .filter(item ->
                        item.getClaim() != null
                                && !item.getClaim().isBlank()
                )
                .map(this::buildSafeBullet)
                .filter(bullet ->
                        bullet != null
                                && !bullet.isBlank()
                )
                .distinct()
                .toList();

        if (bullets.isEmpty()) {
            return """
                    No evidence-backed project content is available yet.

                    Add a verified project or a detailed learning-progress update
                    before generating project bullets.
                    """;
        }

        return String.join("\n", bullets);
    }

    private String buildSafeBullet(
            GeneratedEvidenceResponse item
    ) {
        String skill = item.getClaim().trim();

        if ("VERIFIED".equals(item.getStatus())) {
            return "• Demonstrated exposure to "
                    + skill
                    + " through information present in the uploaded resume.";
        }

        if ("USER_CONFIRMED".equals(item.getStatus())) {
            return "• Currently developing "
                    + skill
                    + " through a self-reported learning activity. "
                    + "[Review before adding to resume]";
        }

        return "";
    }

    private String createProjectBullets(
            List<ProjectEvidence> projects
    ) {
        if (projects.isEmpty()) {
            return """
                    No structured project evidence has been added yet.

                    Add a real project with your actual contribution, skills used,
                    and optional outcome before generating strong project bullets.
                    """;
        }

        return projects.stream()
                .map(this::createProjectBullet)
                .collect(Collectors.joining("\n"));
    }

    private String createProjectBullet(
            ProjectEvidence project
    ) {
        StringBuilder bullet = new StringBuilder();

        bullet.append("• Built ")
                .append(project.getProjectName())
                .append(": ")
                .append(cleanSentence(
                        project.getStudentAction()
                ));

        if (project.getResultText() != null
                && !project.getResultText().isBlank()) {
            bullet.append(" Result: ")
                    .append(cleanSentence(
                            project.getResultText()
                    ));
        }

        if (project.getGithubUrl() != null
                && !project.getGithubUrl().isBlank()) {
            bullet.append(" GitHub: ")
                    .append(project.getGithubUrl().trim());
        }

        if (project.getLiveUrl() != null
                && !project.getLiveUrl().isBlank()) {
            bullet.append(" Live demo: ")
                    .append(project.getLiveUrl().trim());
        }

        return bullet.toString();
    }

    private String cleanSentence(String value) {
        if (value == null || value.isBlank()) {
            return "Project contribution requires confirmation.";
        }

        String cleaned = value
                .replaceAll("\\s+", " ")
                .trim();

        if (cleaned.endsWith(".")) {
            return cleaned;
        }

        return cleaned + ".";
    }

    private String createExperienceGuidance(
            List<GeneratedEvidenceResponse> verifiedEvidence,
            List<ProjectEvidence> projects
    ) {
        if (verifiedEvidence.isEmpty() && projects.isEmpty()) {
            return """
                    No verified employment, internship, or structured project evidence is available.

                    Use only your real internships, volunteering, academic projects,
                    freelance work, or practical learning activities.

                    Do not add unverified job titles, company names, years of experience,
                    certifications, deployment claims, salary claims, or performance metrics.
                    """;
        }

        return """
                Add experience bullets only when they are supported by your real resume,
                internship, project, academic work, or verified learning evidence.

                Do not add unverified company names, job titles, years of experience,
                certifications, deployment claims, salary claims, or numerical metrics.
                """;
    }

    private String calculateEvidenceStatus(
            List<GeneratedEvidenceResponse> allEvidence,
            List<GeneratedEvidenceResponse> verifiedEvidence,
            List<GeneratedEvidenceResponse> draftEvidence,
            List<ProjectEvidence> projects
    ) {
        boolean hasProjects = !projects.isEmpty();

        if (allEvidence.isEmpty() && !hasProjects) {
            return "NO_EVIDENCE";
        }

        if (!allEvidence.isEmpty()
                && verifiedEvidence.size() == allEvidence.size()
                && hasProjects) {
            return "EVIDENCE_SUPPORTED";
        }

        if (!draftEvidence.isEmpty() || hasProjects) {
            return "PARTIALLY_SUPPORTED";
        }

        return "REQUIRES_CONFIRMATION";
    }
}