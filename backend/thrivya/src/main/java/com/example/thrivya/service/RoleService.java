package com.example.thrivya.service;

import com.example.thrivya.dto.response.RoleSuggestionResponse;
import com.example.thrivya.dto.response.SelectedRoleResponse;
import com.example.thrivya.model.CareerProfile;
import com.example.thrivya.model.Role;
import com.example.thrivya.model.RoleSuggestion;
import com.example.thrivya.model.User;
import com.example.thrivya.repository.CareerProfileRepository;
import com.example.thrivya.repository.ProfileSkillRepository;
import com.example.thrivya.repository.RoleRepository;
import com.example.thrivya.repository.RoleSuggestionRepository;
import com.example.thrivya.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final UserRepository userRepository;
    private final CareerProfileRepository profileRepository;
    private final ProfileSkillRepository profileSkillRepository;
    private final RoleRepository roleRepository;
    private final RoleSuggestionRepository suggestionRepository;

    /**
     * Generates a maximum of five role suggestions
     * based on the student's current normalized skills.
     */
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
                        .filter(profileSkill ->
                                profileSkill.getSkill() != null
                        )
                        .map(profileSkill ->
                                profileSkill
                                        .getSkill()
                                        .getCanonicalName()
                                        .toLowerCase(Locale.ROOT)
                                        .trim()
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
                                Comparator
                                        .comparing(
                                                ScoredRole::score
                                        )
                                        .reversed()
                                        .thenComparing(
                                                scoredRole ->
                                                        scoredRole
                                                                .role()
                                                                .getTitle()
                                                                .toLowerCase(
                                                                        Locale.ROOT
                                                                )
                                        )
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

            suggestions.add(suggestion);

            responses.add(
                    buildRoleSuggestionResponse(
                            role,
                            scoredRole.score(),
                            rank,
                            false
                    )
            );

            rank++;
        }

        suggestionRepository.saveAll(suggestions);

        return responses;
    }

    /**
     * Searches career roles using the desired role
     * entered by the student.
     */
    @Transactional
    public List<RoleSuggestionResponse> searchDesiredRole(
            String email,
            String desiredRole
    ) {
        if (desiredRole == null
                || desiredRole.isBlank()) {
            throw new IllegalArgumentException(
                    "Desired role cannot be empty"
            );
        }

        User user = getUser(email);

        CareerProfile profile =
                profileRepository.findByUserId(user.getId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Set up your career profile first"
                                ));

        String cleanedDesiredRole =
                desiredRole.trim();

        profile.setDesiredRole(cleanedDesiredRole);
        profileRepository.save(profile);

        String desiredRoleLower =
                cleanedDesiredRole.toLowerCase(Locale.ROOT);

        List<Role> roles =
                roleRepository.findAll();

        List<ScoredRole> scoredRoles =
                roles.stream()
                        .map(role -> {
                            String roleTitle =
                                    role.getTitle()
                                            .toLowerCase(Locale.ROOT);

                            double score =
                                    calculateTextSimilarity(
                                            desiredRoleLower,
                                            roleTitle
                                    );

                            return new ScoredRole(
                                    role,
                                    score
                            );
                        })
                        .filter(scoredRole ->
                                scoredRole.score() > 0.0
                        )
                        .sorted(
                                Comparator
                                        .comparing(
                                                ScoredRole::score
                                        )
                                        .reversed()
                                        .thenComparing(
                                                scoredRole ->
                                                        scoredRole
                                                                .role()
                                                                .getTitle()
                                                                .toLowerCase(
                                                                        Locale.ROOT
                                                                )
                                        )
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

            suggestions.add(suggestion);

            responses.add(
                    buildRoleSuggestionResponse(
                            role,
                            scoredRole.score(),
                            rank,
                            false
                    )
            );

            rank++;
        }

        suggestionRepository.saveAll(suggestions);

        return responses;
    }

    /**
     * Stores the selected role for the student's profile.
     */
    @Transactional
    public SelectedRoleResponse selectRole(
            String email,
            Long roleId
    ) {
        if (roleId == null) {
            throw new IllegalArgumentException(
                    "Role ID is required"
            );
        }

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

        suggestions.forEach(suggestion -> {
            Role suggestionRole =
                    suggestion.getRole();

            suggestion.setSelected(
                    suggestionRole != null
                            && suggestionRole.getId()
                            .equals(selectedRole.getId())
            );
        });

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

    /**
     * Calculates a simple explainable skill-based score.
     *
     * This is a rule-based baseline for the MVP.
     * Later it can be replaced by embeddings or FastAPI.
     */
    private ScoredRole scoreRole(
            Role role,
            Set<String> userSkillNames
    ) {
        String title =
                role.getTitle()
                        .toLowerCase(Locale.ROOT);

        double score = 0.0;

        String simplifiedTitle =
                title
                        .replace("developer", "")
                        .replace("engineer", "")
                        .trim();

        for (String skill : userSkillNames) {
            if (title.contains(skill)) {
                score += 0.40;
            }

            if (!simplifiedTitle.isBlank()
                    && skill.contains(simplifiedTitle)) {
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

        if (title.contains("backend")
                && userSkillNames.contains("rest api")) {
            score += 0.20;
        }

        if (title.contains("full stack")
                && userSkillNames.contains("react")) {
            score += 0.20;
        }

        if (title.contains("frontend")
                && userSkillNames.contains("react")) {
            score += 0.30;
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

    /**
     * Calculates similarity between the student's desired
     * role text and a stored career role title.
     */
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
                        .filter(word -> !word.isBlank())
                        .collect(Collectors.toSet());

        Set<String> roleWords =
                Arrays.stream(roleTitle.split("\\s+"))
                        .filter(word -> !word.isBlank())
                        .collect(Collectors.toSet());

        if (inputWords.isEmpty()
                || roleWords.isEmpty()) {
            return 0.0;
        }

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

    private RoleSuggestionResponse
    buildRoleSuggestionResponse(
            Role role,
            double score,
            int rank,
            boolean selected
    ) {
        return RoleSuggestionResponse.builder()
                .roleId(role.getId())
                .title(role.getTitle())
                .description(role.getDescription())
                .relevanceScore(score)
                .rankNumber(rank)
                .selected(selected)
                .build();
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