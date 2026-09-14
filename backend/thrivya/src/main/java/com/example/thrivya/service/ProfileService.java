package com.example.thrivya.service;

import com.example.thrivya.dto.response.ProfileInputResponse;
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

import java.util.List;

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

        profile.setInputMode(InputMode.MANUAL_SKILLS);
        profileRepository.save(profile);

        /*
         * For the current MVP, the latest manual input becomes
         * the current active skill profile.
         */
        profileSkillRepository.deleteAllByProfileId(
                profile.getId()
        );

        List<SkillExtractionService.ExtractedSkill>
                extractedSkills =
                skillExtractionService
                        .extractFromManualText(
                                skillsText
                        );

        List<SkillResponse> responses =
                saveSkills(
                        profile,
                        extractedSkills
                );

        return ProfileInputResponse.builder()
                .profileId(profile.getId())
                .inputMode(
                        InputMode.MANUAL_SKILLS.name()
                )
                .desiredRole(
                        profile.getDesiredRole()
                )
                .selectedRoleId(
                        getSelectedRoleId(profile)
                )
                .selectedRoleTitle(
                        getSelectedRoleTitle(profile)
                )
                .skills(responses)
                .message(
                        "Manual skills saved successfully"
                )
                .build();
    }

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

        profile.setInputMode(InputMode.RESUME_UPLOAD);
        profileRepository.save(profile);

        ResumeFileService.StoredResume storedResume =
                resumeFileService.saveAndExtract(file);

        Resume resume =
                Resume.builder()
                        .user(user)
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
                        .build();

        Resume savedResume =
                resumeRepository.save(resume);

        /*
         * The newly uploaded resume becomes the source
         * of the current active skill profile.
         */
        profileSkillRepository.deleteAllByProfileId(
                profile.getId()
        );

        List<SkillExtractionService.ExtractedSkill>
                extractedSkills =
                skillExtractionService
                        .extractFromResumeText(
                                storedResume.extractedText()
                        );

        List<SkillResponse> responses =
                saveSkills(
                        profile,
                        extractedSkills
                );

        return ProfileInputResponse.builder()
                .profileId(profile.getId())
                .resumeId(savedResume.getId())
                .inputMode(
                        InputMode.RESUME_UPLOAD.name()
                )
                .fileName(
                        savedResume.getOriginalFileName()
                )
                .extractedText(
                        savedResume.getExtractedText()
                )
                .desiredRole(
                        profile.getDesiredRole()
                )
                .selectedRoleId(
                        getSelectedRoleId(profile)
                )
                .selectedRoleTitle(
                        getSelectedRoleTitle(profile)
                )
                .skills(responses)
                .message(
                        "Resume uploaded and skills extracted successfully"
                )
                .build();
    }

    @Transactional(readOnly = true)
    public ProfileInputResponse getCurrentProfile(
            String email
    ) {
        User user = getUser(email);

        CareerProfile profile =
                profileRepository.findByUserId(user.getId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Career profile not found"
                                ));

        List<ProfileSkill> profileSkills =
                profileSkillRepository
                        .findAllByProfileId(profile.getId());

        List<SkillResponse> skills =
                profileSkills.stream()
                        .map(this::toSkillResponse)
                        .toList();

        Resume latestResume =
                resumeRepository
                        .findFirstByUserIdOrderByUploadedAtDesc(
                                user.getId()
                        )
                        .orElse(null);

        return ProfileInputResponse.builder()
                .profileId(profile.getId())
                .resumeId(
                        latestResume == null
                                ? null
                                : latestResume.getId()
                )
                .inputMode(
                        profile.getInputMode() == null
                                ? null
                                : profile.getInputMode().name()
                )
                .fileName(
                        latestResume == null
                                ? null
                                : latestResume
                                .getOriginalFileName()
                )
                .extractedText(
                        latestResume == null
                                ? null
                                : latestResume
                                .getExtractedText()
                )
                .desiredRole(
                        profile.getDesiredRole()
                )
                .selectedRoleId(
                        getSelectedRoleId(profile)
                )
                .selectedRoleTitle(
                        getSelectedRoleTitle(profile)
                )
                .skills(skills)
                .message(
                        "Profile loaded successfully"
                )
                .build();
    }

    private List<SkillResponse> saveSkills(
            CareerProfile profile,
            List<SkillExtractionService.ExtractedSkill>
                    extractedSkills
    ) {
        return extractedSkills.stream()
                .map(extractedSkill -> {
                    Skill skill =
                            skillRepository
                                    .findByCanonicalNameIgnoreCase(
                                            extractedSkill.name()
                                    )
                                    .orElseGet(() ->
                                            skillRepository.save(
                                                    Skill.builder()
                                                            .canonicalName(
                                                                    extractedSkill
                                                                            .name()
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

                    return SkillResponse.builder()
                            .name(
                                    skill.getCanonicalName()
                            )
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
                })
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

    private Long getSelectedRoleId(
            CareerProfile profile
    ) {
        if (profile.getSelectedRole() == null) {
            return null;
        }

        return profile
                .getSelectedRole()
                .getId();
    }

    private String getSelectedRoleTitle(
            CareerProfile profile
    ) {
        if (profile.getSelectedRole() == null) {
            return null;
        }

        return profile
                .getSelectedRole()
                .getTitle();
    }

    private CareerProfile getOrCreateProfile(
            User user,
            InputMode inputMode
    ) {
        return profileRepository.findByUserId(user.getId())
                .orElseGet(() ->
                        profileRepository.save(
                                CareerProfile.builder()
                                        .user(user)
                                        .inputMode(inputMode)
                                        .build()
                        )
                );
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Authenticated user not found"
                        ));
    }
}