package com.example.thrivya.service;

import com.example.thrivya.dto.response.RoleSuggestionResponse;
import com.example.thrivya.dto.response.SelectedRoleResponse;
import com.example.thrivya.model.*;
import com.example.thrivya.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final UserRepository userRepository;
    private final CareerProfileRepository profileRepository;
    private final ProfileSkillRepository profileSkillRepository;
    private final RoleRepository roleRepository;
    private final RoleSuggestionRepository suggestionRepository;

    @Transactional
    public List<RoleSuggestionResponse> suggestRoles(
            String email
    ) {
        User user = getUser(email);

        CareerProfile profile =
                profileRepository.findByUserId(user.getId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Set up your career profile first"
                                ));

        Set<String> userSkillNames =
                profileSkillRepository
                        .findAllByProfileId(profile.getId())
                        .stream()
                        .map(profileSkill ->
                                profileSkill
                                        .getSkill()
                                        .getCanonicalName()
                                        .toLowerCase(Locale.ROOT)
                        )
                        .collect(Collectors.toSet());

        if (userSkillNames.isEmpty()) {
            throw new IllegalArgumentException(
                    "Add skills or upload a resume before requesting role suggestions"
            );
        }

        List<Role> roles =
                roleRepository.findAll();

        List<ScoredRole> scoredRoles =
                roles.stream()
                        .map(role ->
                                scoreRole(
                                        role,
                                        userSkillNames
                                )
                        )
                        .sorted(
                                Comparator.comparing(
                                        ScoredRole::score
                                ).reversed()
                        )
                        .limit(5)
                        .toList();

        suggestionRepository.deleteAllByProfileId(
                profile.getId()
        );

        List<RoleSuggestion> suggestions =
                new ArrayList<>();

        List<RoleSuggestionResponse> responses =
                new ArrayList<>();

        int rank = 1;

        for (ScoredRole scoredRole : scoredRoles) {
            Role role = scoredRole.role();

            RoleSuggestion suggestion =
                    RoleSuggestion.builder()
                            .profile(profile)
                            .role(role)
                            .relevanceScore(
                                    scoredRole.score()
                            )
                            .rankNumber(rank)
                            .selected(false)
                            .build();

            suggestionRepository.save(suggestion);

            responses.add(
                    RoleSuggestionResponse.builder()
                            .roleId(role.getId())
                            .title(role.getTitle())
                            .description(role.getDescription())
                            .relevanceScore(
                                    scoredRole.score()
                            )
                            .rankNumber(rank)
                            .selected(false)
                            .build()
            );

            rank++;
        }

        return responses;
    }

    @Transactional
    public List<RoleSuggestionResponse> searchDesiredRole(
            String email,
            String desiredRole
    ) {
        User user = getUser(email);

        CareerProfile profile =
                profileRepository.findByUserId(user.getId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Set up your career profile first"
                                ));

        profile.setDesiredRole(desiredRole.trim());
        profileRepository.save(profile);

        String desiredRoleLower =
                desiredRole.toLowerCase(Locale.ROOT);

        List<Role> roles =
                roleRepository.findAll();

        List<ScoredRole> scoredRoles =
                roles.stream()
                        .map(role -> {
                            double score =
                                    calculateTextSimilarity(
                                            desiredRoleLower,
                                            role.getTitle()
                                                    .toLowerCase(
                                                            Locale.ROOT
                                                    )
                                    );

                            return new ScoredRole(
                                    role,
                                    score
                            );
                        })
                        .filter(scoredRole ->
                                scoredRole.score() > 0
                        )
                        .sorted(
                                Comparator.comparing(
                                        ScoredRole::score
                                ).reversed()
                        )
                        .limit(5)
                        .toList();

        return toResponses(scoredRoles);
    }

    @Transactional
    public SelectedRoleResponse selectRole(
            String email,
            Long roleId
    ) {
        User user = getUser(email);

        CareerProfile profile =
                profileRepository.findByUserId(user.getId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Set up your career profile first"
                                ));

        Role selectedRole =
                roleRepository.findById(roleId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Career role not found"
                                ));

        profile.setSelectedRole(selectedRole);
        profileRepository.save(profile);

        List<RoleSuggestion> suggestions =
                suggestionRepository
                        .findAllByProfileIdOrderByRankNumberAsc(
                                profile.getId()
                        );

        suggestions.forEach(suggestion ->
                suggestion.setSelected(
                        suggestion.getRole()
                                .getId()
                                .equals(roleId)
                )
        );

        suggestionRepository.saveAll(suggestions);

        return SelectedRoleResponse.builder()
                .profileId(profile.getId())
                .roleId(selectedRole.getId())
                .roleTitle(selectedRole.getTitle())
                .message(
                        "Career role selected successfully"
                )
                .build();
    }

    private ScoredRole scoreRole(
            Role role,
            Set<String> userSkillNames
    ) {
        String title =
                role.getTitle()
                        .toLowerCase(Locale.ROOT);

        double score = 0.0;

        for (String skill : userSkillNames) {
            if (title.contains(skill)
                    || skill.contains(
                    title.replace(
                            "developer",
                            ""
                    ).trim()
            )) {
                score += 0.40;
            }
        }

        if (title.contains("java")
                && userSkillNames.contains("java")) {
            score += 0.30;
        }

        if (title.contains("spring")
                && userSkillNames.contains("spring boot")) {
            score += 0.30;
        }

        if (title.contains("full stack")
                && userSkillNames.contains("react")) {
            score += 0.20;
        }

        if (title.contains("data analyst")
                && userSkillNames.contains("sql")) {
            score += 0.30;
        }

        if (title.contains("machine learning")
                && userSkillNames.contains(
                "machine learning"
        )) {
            score += 0.30;
        }

        if (title.contains("devops")
                && userSkillNames.contains("docker")) {
            score += 0.30;
        }

        return new ScoredRole(
                role,
                Math.min(score, 1.0)
        );
    }

    private double calculateTextSimilarity(
            String input,
            String roleTitle
    ) {
        if (roleTitle.equals(input)) {
            return 1.0;
        }

        if (roleTitle.contains(input)
                || input.contains(roleTitle)) {
            return 0.85;
        }

        Set<String> inputWords =
                Arrays.stream(input.split("\\s+"))
                        .collect(Collectors.toSet());

        Set<String> roleWords =
                Arrays.stream(roleTitle.split("\\s+"))
                        .collect(Collectors.toSet());

        Set<String> intersection =
                new HashSet<>(inputWords);

        intersection.retainAll(roleWords);

        if (intersection.isEmpty()) {
            return 0.0;
        }

        Set<String> union =
                new HashSet<>(inputWords);

        union.addAll(roleWords);

        return (double) intersection.size()
                / union.size();
    }

    private List<RoleSuggestionResponse> toResponses(
            List<ScoredRole> scoredRoles
    ) {
        int rank = 1;

        List<RoleSuggestionResponse> responses =
                new ArrayList<>();

        for (ScoredRole scoredRole : scoredRoles) {
            responses.add(
                    RoleSuggestionResponse.builder()
                            .roleId(
                                    scoredRole.role().getId()
                            )
                            .title(
                                    scoredRole.role().getTitle()
                            )
                            .description(
                                    scoredRole.role()
                                            .getDescription()
                            )
                            .relevanceScore(
                                    scoredRole.score()
                            )
                            .rankNumber(rank)
                            .selected(false)
                            .build()
            );

            rank++;
        }

        return responses;
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Authenticated user not found"
                        ));
    }

    private record ScoredRole(
            Role role,
            double score
    ) {
    }
}