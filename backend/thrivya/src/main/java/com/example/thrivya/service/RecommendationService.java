package com.example.thrivya.service;

import com.example.thrivya.dto.response.ResourceResponse;
import com.example.thrivya.model.RecommendationType;
import com.example.thrivya.model.Resource;
import com.example.thrivya.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final ResourceRepository resourceRepository;

    @Transactional(readOnly = true)
    public List<ResourceResponse> getRecommendations(
            String skillName,
            RecommendationType type
    ) {
        List<Resource> storedResources =
                resourceRepository
                        .findAllBySkillCanonicalNameIgnoreCaseAndTypeAndActiveTrueOrderByVerifiedDescIdAsc(
                                skillName,
                                type
                        );

        if (!storedResources.isEmpty()) {
            return storedResources.stream()
                    .limit(10)
                    .map(resource ->
                            toResponse(
                                    resource,
                                    "CURATED"
                            )
                    )
                    .toList();
        }

        return List.of(
                createSearchRecommendation(
                        skillName,
                        type
                )
        );
    }

    private ResourceResponse createSearchRecommendation(
            String skillName,
            RecommendationType type
    ) {
        String encodedSkill =
                UriUtils.encode(
                        skillName,
                        StandardCharsets.UTF_8
                );

        String url;
        String title;
        String provider;

        switch (type) {
            case VIDEO -> {
                url =
                        "https://www.youtube.com/results?search_query="
                                + encodedSkill
                                + "+tutorial";

                title =
                        "Search video tutorials for "
                                + skillName;

                provider = "YouTube Search";
            }

            case AUDIO -> {
                url =
                        "https://open.spotify.com/search/"
                                + encodedSkill;

                title =
                        "Search audio resources for "
                                + skillName;

                provider = "Spotify Search";
            }

            case DOCUMENT -> {
                url =
                        "https://www.google.com/search?q="
                                + encodedSkill
                                + "+official+documentation";

                title =
                        "Search documentation for "
                                + skillName;

                provider = "Web Search";
            }

            default -> throw new IllegalArgumentException(
                    "Unsupported recommendation type"
            );
        }

        return ResourceResponse.builder()
                .skillName(skillName)
                .type(type.name())
                .title(title)
                .url(url)
                .provider(provider)
                .description(
                        "Open a current search result for this skill."
                )
                .difficulty("BEGINNER")
                .verified(false)
                .source("LIVE_SEARCH")
                .build();
    }

    private ResourceResponse toResponse(
            Resource resource,
            String source
    ) {
        return ResourceResponse.builder()
                .id(resource.getId())
                .skillName(
                        resource.getSkill()
                                .getCanonicalName()
                )
                .type(resource.getType().name())
                .title(resource.getTitle())
                .url(resource.getUrl())
                .provider(resource.getProvider())
                .description(resource.getDescription())
                .difficulty(resource.getDifficulty())
                .estimatedMinutes(
                        resource.getEstimatedMinutes()
                )
                .verified(resource.getVerified())
                .source(source)
                .build();
    }
}