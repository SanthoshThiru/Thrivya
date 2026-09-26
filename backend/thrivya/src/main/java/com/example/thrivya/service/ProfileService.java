package com.example.thrivya.service;

import com.example.thrivya.dto.response.ProfileInputResponse;
import com.example.thrivya.dto.response.ResumeHistoryResponse;
import com.example.thrivya.dto.response.ResumeVersionResponse;
import com.example.thrivya.dto.response.SkillResponse;
import com.example.thrivya.model.CareerProfile;
import com.example.thrivya.model.InputMode;
import com.example.thrivya.model.ProfileSkill;
import com.example.thrivya.model.Resume;
import com.example.thrivya.model.Skill;
import com.example.thrivya.model.User;
import com.example.thrivya.repository.CareerProfileRepository;
import com.example.thrivya.repository.ProfileSkillRepository;
import com.example.thrivya.repository.ResumeRepository;
import com.example.thrivya.repository.SkillRepository;
import com.example.thrivya.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final CareerProfileRepository profileRepository;
    private final ResumeRepository resumeRepository;
    private final SkillRepository skillRepository;
    private final ProfileSkillRepository profileSkillRepository;
    private final SkillExtractionService skillExtractionService;
    private final ResumeFileService resumeFileService;
    private final SkillGapService skillGapService;

    /*
     * Manual skill input.
     */
    @Transactional
    public ProfileInputResponse saveManualSkills(
            String email,
            String skillsText
    ) {
        User user = getUser(email);

        CareerProfile profile =
                getOrCreateProfile(
                        user,
                        InputMode.MANUAL_SKILLS
                );

        profile.setInputMode(
                InputMode.MANUAL_SKILLS
        );

        CareerProfile savedProfile =
                profileRepository.save(profile);

        List<SkillExtractionService.ExtractedSkill>
                extractedSkills =
                skillExtractionService
                        .extractFromManualText(
                                skillsText
                        );

        replaceCurrentProfileSkills(
                savedProfile,
                extractedSkills
        );

        recalculateSkillGapsIfRoleExists(
                email,
                savedProfile
        );

        return buildProfileResponse(
                savedProfile,
                getActiveResume(user.getId()),
                InputMode.MANUAL_SKILLS,
                "Manual skills saved successfully"
        );
    }

    /*
     * Resume upload and version creation.
     */
    @Transactional
    public ProfileInputResponse saveResume(
            String email,
            MultipartFile file
    ) {
        User user = getUser(email);

        CareerProfile profile =
                getOrCreateProfile(
                        user,
                        InputMode.RESUME_UPLOAD
                );

        profile.setInputMode(
                InputMode.RESUME_UPLOAD
        );

        CareerProfile savedProfile =
                profileRepository.save(profile);

        ResumeFileService.StoredResume storedResume =
                resumeFileService.saveAndExtract(file);

        /*
         * Mark previous versions as inactive.
         * Do not delete them.
         */
        deactivateOldResumes(user.getId());

        int nextVersion =
                getNextResumeVersion(user.getId());

        Resume newResume =
                Resume.builder()
                        .user(user)
                        .versionNumber(nextVersion)
                        .originalFileName(
                                storedResume.originalFileName()
                        )
                        .storedFileName(
                                storedResume.storedFileName()
                        )
                        .filePath(
                                storedResume.filePath()
                        )
                        .contentType(
                                storedResume.contentType()
                        )
                        .extractedText(
                                storedResume.extractedText()
                        )
                        .active(true)
                        .build();

        Resume savedResume =
                resumeRepository.save(newResume);

        List<SkillExtractionService.ExtractedSkill>
                extractedSkills =
                skillExtractionService
                        .extractFromResumeText(
                                storedResume.extractedText()
                        );

        replaceCurrentProfileSkills(
                savedProfile,
                extractedSkills
        );

        /*
         * Recalculate only if the student has already selected
         * a career role.
         */
        recalculateSkillGapsIfRoleExists(
                email,
                savedProfile
        );

        return buildProfileResponse(
                savedProfile,
                savedResume,
                InputMode.RESUME_UPLOAD,
                "Resume version "
                        + savedResume.getVersionNumber()
                        + " uploaded and skills extracted successfully"
        );
    }

    /*
     * Get current active profile.
     */
    @Transactional(readOnly = true)
    public ProfileInputResponse getCurrentProfile(
            String email
    ) {
        User user = getUser(email);

        CareerProfile profile =
                profileRepository
                        .findByUserIdWithSelectedRole(
                                user.getId()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Career profile not found"
                                )
                        );

        Resume activeResume =
                getActiveResume(user.getId());

        return buildProfileResponse(
                profile,
                activeResume,
                profile.getInputMode(),
                "Current profile loaded successfully"
        );
    }

    /*
     * Get all uploaded resume versions.
     */
    @Transactional(readOnly = true)
    public ResumeHistoryResponse getResumeHistory(
            String email
    ) {
        User user = getUser(email);

        List<Resume> resumes =
                resumeRepository
                        .findAllByUserIdOrderByVersionNumberDesc(
                                user.getId()
                        );

        List<ResumeVersionResponse> versions =
                resumes.stream()
                        .filter(Objects::nonNull)
                        .map(resume ->
                                ResumeVersionResponse.builder()
                                        .resumeId(
                                                resume.getId()
                                        )
                                        .versionNumber(
                                                resume.getVersionNumber()
                                        )
                                        .originalFileName(
                                                resume.getOriginalFileName()
                                        )
                                        .uploadedAt(
                                                resume.getUploadedAt()
                                        )
                                        .active(
                                                Boolean.TRUE.equals(
                                                        resume.getActive()
                                                )
                                        )
                                        .build()
                        )
                        .toList();

        Resume activeResume =
                resumes.stream()
                        .filter(Objects::nonNull)
                        .filter(resume ->
                                Boolean.TRUE.equals(
                                        resume.getActive()
                                )
                        )
                        .findFirst()
                        .orElse(null);

        return ResumeHistoryResponse.builder()
                .activeResumeId(
                        activeResume == null
                                ? null
                                : activeResume.getId()
                )
                .activeVersionNumber(
                        activeResume == null
                                ? null
                                : activeResume
                                .getVersionNumber()
                )
                .versions(versions)
                .build();
    }

    /*
     * Build a DTO while the transaction is active.
     * This prevents lazy proxy errors.
     */
    private ProfileInputResponse buildProfileResponse(
            CareerProfile profile,
            Resume resume,
            InputMode inputMode,
            String message
    ) {
        List<SkillResponse> skills =
                getSkillResponses(profile.getId());

        Long selectedRoleId =
                null;

        String selectedRoleTitle =
                null;

        if (profile.getSelectedRole() != null) {
            selectedRoleId =
                    profile.getSelectedRole().getId();

            selectedRoleTitle =
                    profile.getSelectedRole().getTitle();
        }

        return ProfileInputResponse.builder()
                .profileId(profile.getId())
                .resumeId(
                        resume == null
                                ? null
                                : resume.getId()
                )
                .inputMode(
                        inputMode == null
                                ? null
                                : inputMode.name()
                )
                .fileName(
                        resume == null
                                ? null
                                : resume.getOriginalFileName()
                )
                .extractedText(
                        resume == null
                                ? null
                                : resume.getExtractedText()
                )
                .desiredRole(
                        profile.getDesiredRole()
                )
                .selectedRoleId(selectedRoleId)
                .selectedRoleTitle(selectedRoleTitle)
                .skills(skills)
                .message(message)
                .build();
    }

    /*
     * Mark all previous resumes inactive.
     */
    private void deactivateOldResumes(
            Long userId
    ) {
        List<Resume> oldResumes =
                resumeRepository
                        .findAllByUserIdOrderByVersionNumberDesc(
                                userId
                        );

        if (oldResumes.isEmpty()) {
            return;
        }

        oldResumes.forEach(resume ->
                resume.setActive(false)
        );

        resumeRepository.saveAll(oldResumes);
        resumeRepository.flush();
    }

    /*
     * Generate the next resume version number.
     */
    private int getNextResumeVersion(
            Long userId
    ) {
        long totalVersions =
                resumeRepository.countByUserId(userId);

        return Math.toIntExact(totalVersions + 1);
    }

    /*
     * Load the latest active resume.
     */
    private Resume getActiveResume(
            Long userId
    ) {
        return resumeRepository
                .findFirstByUserIdAndActiveTrueOrderByVersionNumberDesc(
                        userId
                )
                .orElse(null);
    }

    /*
     * Recalculate gaps only after role selection.
     */
    private void recalculateSkillGapsIfRoleExists(
            String email,
            CareerProfile profile
    ) {
        if (profile.getSelectedRole() == null) {
            return;
        }

        skillGapService.recalculateForCurrentRole(
                email
        );
    }

    /*
     * Replace current active skills.
     * Historical resume and progress records remain preserved.
     */
    private void replaceCurrentProfileSkills(
            CareerProfile profile,
            List<SkillExtractionService.ExtractedSkill>
                    extractedSkills
    ) {
        if (profile == null
                || profile.getId() == null) {
            throw new IllegalArgumentException(
                    "Career profile is required"
            );
        }

        profileSkillRepository.deleteAllByProfileId(
                profile.getId()
        );

        profileSkillRepository.flush();

        LinkedHashMap<
                String,
                SkillExtractionService.ExtractedSkill
                > uniqueSkills =
                new LinkedHashMap<>();

        if (extractedSkills != null) {
            for (
                    SkillExtractionService.ExtractedSkill
                            extractedSkill
                    : extractedSkills
            ) {
                if (extractedSkill == null
                        || extractedSkill.name() == null
                        || extractedSkill.name().isBlank()) {
                    continue;
                }

                String key =
                        extractedSkill.name()
                                .trim()
                                .toLowerCase(
                                        Locale.ROOT
                                );

                SkillExtractionService.ExtractedSkill
                        existingSkill =
                        uniqueSkills.get(key);

                if (existingSkill == null
                        || confidenceOf(extractedSkill)
                        > confidenceOf(existingSkill)) {
                    uniqueSkills.put(
                            key,
                            extractedSkill
                    );
                }
            }
        }

        for (
                SkillExtractionService.ExtractedSkill
                        extractedSkill
                : uniqueSkills.values()
        ) {
            Skill skill =
                    skillRepository
                            .findByCanonicalNameIgnoreCase(
                                    extractedSkill.name()
                            )
                            .orElseGet(() ->
                                    skillRepository.save(
                                            Skill.builder()
                                                    .canonicalName(
                                                            extractedSkill.name()
                                                    )
                                                    .build()
                                    )
                            );

            ProfileSkill profileSkill =
                    ProfileSkill.builder()
                            .profile(profile)
                            .skill(skill)
                            .source(
                                    extractedSkill.source()
                            )
                            .confidence(
                                    extractedSkill.confidence()
                            )
                            .evidenceText(
                                    extractedSkill.evidenceText()
                            )
                            .build();

            profileSkillRepository.save(
                    profileSkill
            );
        }

        profileSkillRepository.flush();
    }

    /*
     * Convert active profile skills to response DTOs.
     */
    private List<SkillResponse> getSkillResponses(
            Long profileId
    ) {
        return profileSkillRepository
                .findAllByProfileId(profileId)
                .stream()
                .filter(Objects::nonNull)
                .filter(profileSkill ->
                        profileSkill.getSkill() != null
                )
                .map(this::toSkillResponse)
                .toList();
    }

    private SkillResponse toSkillResponse(
            ProfileSkill profileSkill
    ) {
        return SkillResponse.builder()
                .name(
                        profileSkill
                                .getSkill()
                                .getCanonicalName()
                )
                .source(
                        profileSkill.getSource()
                )
                .confidence(
                        profileSkill.getConfidence()
                )
                .evidenceText(
                        profileSkill.getEvidenceText()
                )
                .build();
    }

    /*
     * Create or retrieve a profile.
     */
    private CareerProfile getOrCreateProfile(
            User user,
            InputMode inputMode
    ) {
        Optional<CareerProfile> existingProfile =
                profileRepository.findByUserId(
                        user.getId()
                );

        if (existingProfile.isPresent()) {
            return existingProfile.get();
        }

        return profileRepository.save(
                CareerProfile.builder()
                        .user(user)
                        .inputMode(inputMode)
                        .build()
        );
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Authenticated user not found"
                        )
                );
    }

    private double confidenceOf(
            SkillExtractionService.ExtractedSkill skill
    ) {
        if (skill == null
                || skill.confidence() == null) {
            return 0.0;
        }

        return Math.max(
                0.0,
                Math.min(
                        1.0,
                        skill.confidence()
                )
        );
    }
}