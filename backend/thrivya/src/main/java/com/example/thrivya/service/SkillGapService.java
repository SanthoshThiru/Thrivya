package com.example.thrivya.service;

import com.example.thrivya.dto.response.SkillGapHistoryItemResponse;
import com.example.thrivya.dto.response.SkillGapHistoryResponse;
import com.example.thrivya.dto.response.SkillGapResponse;
import com.example.thrivya.dto.response.SkillGapSummaryResponse;
import com.example.thrivya.model.CareerProfile;
import com.example.thrivya.model.ProfileSkill;
import com.example.thrivya.model.Resume;
import com.example.thrivya.model.Role;
import com.example.thrivya.model.RoleSkill;
import com.example.thrivya.model.Skill;
import com.example.thrivya.model.SkillGap;
import com.example.thrivya.model.User;
import com.example.thrivya.repository.CareerProfileRepository;
import com.example.thrivya.repository.ProfileSkillRepository;
import com.example.thrivya.repository.ResumeRepository;
import com.example.thrivya.repository.RoleSkillRepository;
import com.example.thrivya.repository.SkillGapRepository;
import com.example.thrivya.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillGapService {

    private final UserRepository userRepository;
    private final CareerProfileRepository profileRepository;
    private final ProfileSkillRepository profileSkillRepository;
    private final ResumeRepository resumeRepository;
    private final RoleSkillRepository roleSkillRepository;
    private final SkillGapRepository skillGapRepository;

    @Transactional
    public SkillGapSummaryResponse recalculateForCurrentRole(
            String email
    ) {
        User user = getUser(email);

        CareerProfile profile =
                profileRepository
                        .findByUserIdForSkillGapUpdate(user.getId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Career profile not found"
                                ));

        Role role = profile.getSelectedRole();

        if (role == null) {
            throw new IllegalArgumentException(
                    "Please select a role before calculating skill gaps"
            );
        }

        Long profileId = profile.getId();
        Long roleId = role.getId();

        Resume activeResume =
                resumeRepository
                        .findFirstByUserIdAndActiveTrueOrderByVersionNumberDesc(
                                user.getId()
                        )
                        .orElse(null);

        List<RoleSkill> requiredSkills =
                roleSkillRepository
                        .findAllByRoleIdWithSkillAndRole(roleId);

        if (requiredSkills.isEmpty()) {
            throw new IllegalArgumentException(
                    "No required skills are configured for this role"
            );
        }

        List<ProfileSkill> profileSkills =
                profileSkillRepository
                        .findAllByProfileIdWithSkill(profileId);

        Map<Long, ProfileSkill> currentSkills =
                profileSkills.stream()
                        .filter(Objects::nonNull)
                        .filter(profileSkill ->
                                profileSkill.getSkill() != null
                        )
                        .filter(profileSkill ->
                                profileSkill.getSkill().getId() != null
                        )
                        .collect(
                                Collectors.toMap(
                                        profileSkill ->
                                                profileSkill
                                                        .getSkill()
                                                        .getId(),
                                        Function.identity(),
                                        this::keepHigherConfidence
                                )
                        );

        Integer latestVersion =
                skillGapRepository.findLatestAnalysisVersion(
                        profileId,
                        roleId
                );

        int nextAnalysisVersion =
                latestVersion == null
                        ? 1
                        : latestVersion + 1;

        LocalDateTime calculatedAt =
                LocalDateTime.now();

        List<SkillGap> gaps =
                new ArrayList<>();

        for (RoleSkill roleSkill : requiredSkills) {
            if (roleSkill == null
                    || roleSkill.getSkill() == null
                    || roleSkill.getSkill().getId() == null) {
                continue;
            }

            Skill requiredSkill =
                    roleSkill.getSkill();

            ProfileSkill profileSkill =
                    currentSkills.get(
                            requiredSkill.getId()
                    );

            double currentLevel =
                    calculateCurrentLevel(profileSkill);

            double targetLevel = 1.0;

            double gapPercentage =
                    round(
                            clamp(
                                    (targetLevel - currentLevel)
                                            * 100.0,
                                    0.0,
                                    100.0
                            )
                    );

            String status =
                    calculateStatus(currentLevel);

            String rationale =
                    buildRationale(
                            requiredSkill,
                            roleSkill,
                            profileSkill,
                            gapPercentage
                    );

            SkillGap gap =
                    SkillGap.builder()
                            .profile(profile)
                            .role(role)
                            .skill(requiredSkill)
                            .resume(activeResume)
                            .analysisVersion(
                                    nextAnalysisVersion
                            )
                            .currentLevel(currentLevel)
                            .targetLevel(targetLevel)
                            .gapPercentage(gapPercentage)
                            .priorityLevel(
                                    roleSkill.getPriorityLevel()
                            )
                            .status(status)
                            .rationale(rationale)
                            .calculatedAt(calculatedAt)
                            .build();

            gaps.add(gap);
        }

        if (gaps.isEmpty()) {
            throw new IllegalArgumentException(
                    "No valid role skills were found for analysis"
            );
        }

        List<SkillGap> savedGaps =
                skillGapRepository.saveAll(gaps);

        sortGaps(savedGaps);

        return toSummary(
                profile,
                role,
                savedGaps,
                profileSkills
        );
    }

    @Transactional(readOnly = true)
    public SkillGapSummaryResponse getCurrentGaps(
            String email
    ) {
        User user = getUser(email);

        CareerProfile profile =
                profileRepository
                        .findByUserIdWithSelectedRole(user.getId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Career profile not found"
                                ));

        Role role = profile.getSelectedRole();

        if (role == null) {
            throw new IllegalArgumentException(
                    "No selected role found"
            );
        }

        Long profileId = profile.getId();
        Long roleId = role.getId();

        List<SkillGap> latestGaps =
                loadLatestGaps(
                        profileId,
                        roleId
                );

        if (latestGaps.isEmpty()) {
            throw new IllegalArgumentException(
                    "Skill gaps have not been calculated yet"
            );
        }

        List<ProfileSkill> profileSkills =
                profileSkillRepository
                        .findAllByProfileIdWithSkill(profileId);

        return toSummary(
                profile,
                role,
                latestGaps,
                profileSkills
        );
    }

    @Transactional(readOnly = true)
    public SkillGapHistoryResponse getHistory(
            String email
    ) {
        User user = getUser(email);

        CareerProfile profile =
                profileRepository
                        .findByUserIdWithSelectedRole(user.getId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Career profile not found"
                                ));

        Role role = profile.getSelectedRole();

        if (role == null) {
            throw new IllegalArgumentException(
                    "No selected role found"
            );
        }

        List<SkillGap> allRows =
                skillGapRepository.findHistoryWithRoleAndResume(
                        profile.getId(),
                        role.getId()
                );

        if (allRows.isEmpty()) {
            return SkillGapHistoryResponse.builder()
                    .roleTitle(role.getTitle())
                    .history(List.of())
                    .build();
        }

        Map<Integer, List<SkillGap>> grouped =
                allRows.stream()
                        .filter(Objects::nonNull)
                        .filter(gap ->
                                gap.getAnalysisVersion() != null
                        )
                        .collect(
                                Collectors.groupingBy(
                                        SkillGap::getAnalysisVersion,
                                        LinkedHashMap::new,
                                        Collectors.toList()
                                )
                        );

        List<SkillGapHistoryItemResponse> history =
                grouped.entrySet()
                        .stream()
                        .sorted(
                                Map.Entry
                                        .<Integer, List<SkillGap>>
                                                comparingByKey()
                                        .reversed()
                        )
                        .map(entry ->
                                createHistoryItem(
                                        role,
                                        entry.getKey(),
                                        entry.getValue()
                                )
                        )
                        .toList();

        return SkillGapHistoryResponse.builder()
                .roleTitle(role.getTitle())
                .history(history)
                .build();
    }

    private List<SkillGap> loadLatestGaps(
            Long profileId,
            Long roleId
    ) {
        Integer latestVersion =
                skillGapRepository.findLatestAnalysisVersion(
                        profileId,
                        roleId
                );

        if (latestVersion == null
                || latestVersion <= 0) {
            return List.of();
        }

        List<SkillGap> latestGaps =
                skillGapRepository
                        .findAllByProfileIdAndRoleIdAndAnalysisVersionOrderByGapPercentageDescPriorityLevelAsc(
                                profileId,
                                roleId,
                                latestVersion
                        );

        sortGaps(latestGaps);

        return latestGaps;
    }

    private SkillGapHistoryItemResponse createHistoryItem(
            Role role,
            Integer analysisVersion,
            List<SkillGap> rows
    ) {
        double averageGap =
                rows.stream()
                        .mapToDouble(gap ->
                                safeNumber(
                                        gap.getGapPercentage()
                                )
                        )
                        .average()
                        .orElse(0.0);

        SkillGap representative =
                rows.stream()
                        .max(
                                Comparator.comparing(
                                        SkillGap::getCalculatedAt,
                                        Comparator.nullsLast(
                                                Comparator.naturalOrder()
                                        )
                                )
                        )
                        .orElse(rows.get(0));

        Resume resume =
                representative.getResume();

        return SkillGapHistoryItemResponse.builder()
                .analysisVersion(analysisVersion)
                .resumeId(
                        resume == null
                                ? null
                                : resume.getId()
                )
                .resumeVersion(
                        resume == null
                                ? null
                                : resume.getVersionNumber()
                )
                .roleTitle(role.getTitle())
                .averageGapPercentage(
                        round(averageGap)
                )
                .readinessPercentage(
                        round(
                                clamp(
                                        100.0 - averageGap,
                                        0.0,
                                        100.0
                                )
                        )
                )
                .calculatedAt(
                        representative.getCalculatedAt()
                )
                .build();
    }

    private SkillGapSummaryResponse toSummary(
            CareerProfile profile,
            Role role,
            List<SkillGap> gaps,
            List<ProfileSkill> profileSkills
    ) {
        Map<Long, ProfileSkill> profileSkillMap =
                profileSkills.stream()
                        .filter(Objects::nonNull)
                        .filter(profileSkill ->
                                profileSkill.getSkill() != null
                        )
                        .filter(profileSkill ->
                                profileSkill.getSkill().getId() != null
                        )
                        .collect(
                                Collectors.toMap(
                                        profileSkill ->
                                                profileSkill
                                                        .getSkill()
                                                        .getId(),
                                        Function.identity(),
                                        this::keepHigherConfidence
                                )
                        );

        List<SkillGapResponse> responses =
                gaps.stream()
                        .filter(Objects::nonNull)
                        .sorted(
                                Comparator
                                        .comparing(
                                                SkillGap::getGapPercentage,
                                                Comparator.nullsLast(
                                                        Comparator.reverseOrder()
                                                )
                                        )
                                        .thenComparing(
                                                SkillGap::getPriorityLevel,
                                                Comparator.nullsLast(
                                                        Comparator.naturalOrder()
                                                )
                                        )
                        )
                        .map(gap -> {
                            Skill skill =
                                    gap.getSkill();

                            Resume resume =
                                    gap.getResume();

                            ProfileSkill profileSkill =
                                    skill == null
                                            ? null
                                            : profileSkillMap.get(
                                            skill.getId()
                                    );

                            return SkillGapResponse.builder()
                                    .skillGapId(gap.getId())
                                    .skillId(
                                            skill == null
                                                    ? null
                                                    : skill.getId()
                                    )
                                    .skillName(
                                            skill == null
                                                    ? "Unknown skill"
                                                    : skill.getCanonicalName()
                                    )
                                    .roleTitle(role.getTitle())
                                    .resumeId(
                                            resume == null
                                                    ? null
                                                    : resume.getId()
                                    )
                                    .resumeVersion(
                                            resume == null
                                                    ? null
                                                    : resume.getVersionNumber()
                                    )
                                    .analysisVersion(
                                            gap.getAnalysisVersion()
                                    )
                                    .priorityLevel(
                                            gap.getPriorityLevel()
                                    )
                                    .learningStage(
                                            learningStage(
                                                    gap.getPriorityLevel()
                                            )
                                    )
                                    .currentLevel(
                                            safeNumber(
                                                    gap.getCurrentLevel()
                                            )
                                    )
                                    .targetLevel(
                                            safeNumber(
                                                    gap.getTargetLevel()
                                            )
                                    )
                                    .gapPercentage(
                                            safeNumber(
                                                    gap.getGapPercentage()
                                            )
                                    )
                                    .status(gap.getStatus())
                                    .rationale(gap.getRationale())
                                    .evidenceText(
                                            profileSkill == null
                                                    ? null
                                                    : profileSkill
                                                    .getEvidenceText()
                                    )
                                    .build();
                        })
                        .toList();

        double averageGap =
                responses.stream()
                        .mapToDouble(response ->
                                safeNumber(
                                        response.getGapPercentage()
                                )
                        )
                        .average()
                        .orElse(0.0);

        double readiness =
                round(
                        clamp(
                                100.0 - averageGap,
                                0.0,
                                100.0
                        )
                );

        return SkillGapSummaryResponse.builder()
                .profileId(profile.getId())
                .roleId(role.getId())
                .roleTitle(role.getTitle())
                .overallReadinessPercentage(readiness)
                .overallGapPercentage(
                        round(averageGap)
                )
                .gaps(responses)
                .build();
    }

    private void sortGaps(
            List<SkillGap> gaps
    ) {
        gaps.sort(
                Comparator
                        .comparing(
                                SkillGap::getGapPercentage,
                                Comparator.nullsLast(
                                        Comparator.reverseOrder()
                                )
                        )
                        .thenComparing(
                                SkillGap::getPriorityLevel,
                                Comparator.nullsLast(
                                        Comparator.naturalOrder()
                                )
                        )
        );
    }

    private ProfileSkill keepHigherConfidence(
            ProfileSkill first,
            ProfileSkill second
    ) {
        return confidenceOf(second)
                > confidenceOf(first)
                ? second
                : first;
    }

    private double confidenceOf(
            ProfileSkill profileSkill
    ) {
        if (profileSkill == null
                || profileSkill.getConfidence() == null) {
            return 0.0;
        }

        return clamp(
                profileSkill.getConfidence(),
                0.0,
                1.0
        );
    }

    private double calculateCurrentLevel(
            ProfileSkill profileSkill
    ) {
        if (profileSkill == null) {
            return 0.0;
        }

        if (profileSkill.getConfidence() == null) {
            return 1.0;
        }

        return clamp(
                profileSkill.getConfidence(),
                0.0,
                1.0
        );
    }

    private String calculateStatus(
            double currentLevel
    ) {
        if (currentLevel >= 0.80) {
            return "READY";
        }

        if (currentLevel >= 0.40) {
            return "PARTIAL";
        }

        return "MISSING";
    }

    private String learningStage(
            Integer priorityLevel
    ) {
        if (priorityLevel == null
                || priorityLevel <= 2) {
            return "FOUNDATION";
        }

        if (priorityLevel <= 4) {
            return "INTERMEDIATE";
        }

        return "ADVANCED";
    }

    private String buildRationale(
            Skill skill,
            RoleSkill roleSkill,
            ProfileSkill profileSkill,
            double gapPercentage
    ) {
        String skillName =
                skill == null
                        ? "This skill"
                        : skill.getCanonicalName();

        String roleTitle =
                roleSkill == null
                        || roleSkill.getRole() == null
                        ? "the selected role"
                        : roleSkill.getRole().getTitle();

        if (profileSkill == null) {
            return skillName
                    + " is required for "
                    + roleTitle
                    + " but was not found in your current profile.";
        }

        if (gapPercentage > 0.0) {
            return skillName
                    + " is present, but its evidence confidence is "
                    + profileSkill.getConfidence()
                    + ". Strengthen it through a project or verified learning evidence.";
        }

        return skillName
                + " is present with sufficient evidence.";
    }

    private double safeNumber(
            Double value
    ) {
        return value == null
                ? 0.0
                : value;
    }

    private double clamp(
            double value,
            double minimum,
            double maximum
    ) {
        return Math.max(
                minimum,
                Math.min(
                        maximum,
                        value
                )
        );
    }

    private double round(
            double value
    ) {
        return Math.round(value * 100.0)
                / 100.0;
    }

    private User getUser(
            String email
    ) {
        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Authenticated user not found"
                        ));
    }
}