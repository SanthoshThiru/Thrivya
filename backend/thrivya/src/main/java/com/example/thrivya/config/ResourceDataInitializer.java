package com.example.thrivya.config;

import com.example.thrivya.model.*;
import com.example.thrivya.repository.ResourceRepository;
import com.example.thrivya.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class ResourceDataInitializer {

    private final SkillRepository skillRepository;
    private final ResourceRepository resourceRepository;

    @Bean
    public CommandLineRunner seedResources() {
        return args -> {
            addResources(
                    "Java",
                    List.of(
                            resource(
                                    RecommendationType.DOCUMENT,
                                    "Official Java Documentation",
                                    "https://docs.oracle.com/en/java/",
                                    "Oracle",
                                    "Official Java language and platform documentation.",
                                    "BEGINNER",
                                    120
                            ),
                            resource(
                                    RecommendationType.VIDEO,
                                    "Java Programming Tutorial",
                                    "https://www.youtube.com/results?search_query=Java+programming+tutorial",
                                    "YouTube",
                                    "Video search for Java programming fundamentals.",
                                    "BEGINNER",
                                    180
                            ),
                            resource(
                                    RecommendationType.AUDIO,
                                    "Java Programming Podcast Search",
                                    "https://open.spotify.com/search/java%20programming",
                                    "Spotify",
                                    "Audio search for Java programming content.",
                                    "BEGINNER",
                                    60
                            )
                    )
            );

            addResources(
                    "Spring Boot",
                    List.of(
                            resource(
                                    RecommendationType.DOCUMENT,
                                    "Spring Boot Reference Documentation",
                                    "https://docs.spring.io/spring-boot/",
                                    "Spring",
                                    "Official Spring Boot documentation.",
                                    "INTERMEDIATE",
                                    150
                            ),
                            resource(
                                    RecommendationType.VIDEO,
                                    "Spring Boot Tutorial Search",
                                    "https://www.youtube.com/results?search_query=Spring+Boot+tutorial",
                                    "YouTube",
                                    "Video search for Spring Boot development.",
                                    "INTERMEDIATE",
                                    180
                            ),
                            resource(
                                    RecommendationType.AUDIO,
                                    "Spring Boot Podcast Search",
                                    "https://open.spotify.com/search/spring%20boot",
                                    "Spotify",
                                    "Audio search for Spring Boot content.",
                                    "INTERMEDIATE",
                                    60
                            )
                    )
            );

            addResources(
                    "Docker",
                    List.of(
                            resource(
                                    RecommendationType.DOCUMENT,
                                    "Docker Get Started Guide",
                                    "https://docs.docker.com/get-started/",
                                    "Docker",
                                    "Official Docker beginner documentation.",
                                    "BEGINNER",
                                    90
                            ),
                            resource(
                                    RecommendationType.VIDEO,
                                    "Docker Beginner Tutorial Search",
                                    "https://www.youtube.com/results?search_query=Docker+beginner+tutorial",
                                    "YouTube",
                                    "Video search for Docker fundamentals.",
                                    "BEGINNER",
                                    150
                            ),
                            resource(
                                    RecommendationType.AUDIO,
                                    "Docker Podcast Search",
                                    "https://open.spotify.com/search/docker",
                                    "Spotify",
                                    "Audio search for Docker topics.",
                                    "BEGINNER",
                                    60
                            )
                    )
            );
        };
    }

    private void addResources(
            String skillName,
            List<ResourceSeed> resources
    ) {
        Skill skill =
                skillRepository
                        .findByCanonicalNameIgnoreCase(
                                skillName
                        )
                        .orElse(null);

        if (skill == null) {
            return;
        }

        for (ResourceSeed seed : resources) {
            boolean exists =
                    resourceRepository
                            .findAllBySkillCanonicalNameIgnoreCaseAndTypeAndActiveTrueOrderByVerifiedDescIdAsc(
                                    skillName,
                                    seed.type()
                            )
                            .stream()
                            .anyMatch(resource ->
                                    resource.getUrl()
                                            .equals(seed.url())
                            );

            if (!exists) {
                resourceRepository.save(
                        Resource.builder()
                                .skill(skill)
                                .type(seed.type())
                                .title(seed.title())
                                .url(seed.url())
                                .provider(seed.provider())
                                .description(seed.description())
                                .difficulty(seed.difficulty())
                                .estimatedMinutes(
                                        seed.estimatedMinutes()
                                )
                                .verified(true)
                                .lastVerifiedAt(
                                        LocalDateTime.now()
                                )
                                .active(true)
                                .build()
                );
            }
        }
    }

    private ResourceSeed resource(
            RecommendationType type,
            String title,
            String url,
            String provider,
            String description,
            String difficulty,
            Integer estimatedMinutes
    ) {
        return new ResourceSeed(
                type,
                title,
                url,
                provider,
                description,
                difficulty,
                estimatedMinutes
        );
    }

    private record ResourceSeed(
            RecommendationType type,
            String title,
            String url,
            String provider,
            String description,
            String difficulty,
            Integer estimatedMinutes
    ) {
    }
}