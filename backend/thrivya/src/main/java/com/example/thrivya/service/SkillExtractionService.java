package com.example.thrivya.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@Service
public class SkillExtractionService {

    private static final Map<String, String> CANONICAL_SKILLS =
            createSkillMap();

    public List<ExtractedSkill> extractFromManualText(
            String skillsText
    ) {
        if (skillsText == null
                || skillsText.isBlank()) {
            return List.of();
        }

        Map<String, ExtractedSkill> uniqueSkills =
                new LinkedHashMap<>();

        String[] enteredParts =
                skillsText.split("[,;\\n|]");

        for (String part : enteredParts) {
            String normalized =
                    normalizeSkill(part);

            if (normalized == null) {
                continue;
            }

            String key =
                    normalized.toLowerCase(Locale.ROOT);

            uniqueSkills.putIfAbsent(
                    key,
                    new ExtractedSkill(
                            normalized,
                            "MANUAL_INPUT",
                            1.0,
                            "Entered by the user"
                    )
            );
        }

        return new ArrayList<>(
                uniqueSkills.values()
        );
    }

    public List<ExtractedSkill> extractFromResumeText(
            String resumeText
    ) {
        if (resumeText == null
                || resumeText.isBlank()) {
            return List.of();
        }

        String lowerText =
                resumeText.toLowerCase(Locale.ROOT);

        Map<String, ExtractedSkill> uniqueSkills =
                new LinkedHashMap<>();

        for (
                Map.Entry<String, String> entry
                : CANONICAL_SKILLS.entrySet()
        ) {
            String alias = entry.getKey();
            String canonicalName = entry.getValue();

            Pattern pattern =
                    Pattern.compile(
                            "(?<![a-z0-9])"
                                    + Pattern.quote(alias)
                                    + "(?![a-z0-9])",
                            Pattern.CASE_INSENSITIVE
                    );

            if (!pattern.matcher(lowerText).find()) {
                continue;
            }

            String key =
                    canonicalName.toLowerCase(
                            Locale.ROOT
                    );

            uniqueSkills.putIfAbsent(
                    key,
                    new ExtractedSkill(
                            canonicalName,
                            "RESUME_TEXT",
                            0.90,
                            findEvidence(
                                    resumeText,
                                    alias
                            )
                    )
            );
        }

        return new ArrayList<>(
                uniqueSkills.values()
        );
    }

    private String normalizeSkill(
            String rawSkill
    ) {
        if (rawSkill == null
                || rawSkill.isBlank()) {
            return null;
        }

        String cleaned =
                rawSkill
                        .trim()
                        .toLowerCase(Locale.ROOT)
                        .replaceAll("\\s+", " ");

        if (cleaned.isBlank()) {
            return null;
        }

        String canonical =
                CANONICAL_SKILLS.get(cleaned);

        if (canonical != null) {
            return canonical;
        }

        return toTitleCase(
                rawSkill.trim()
        );
    }

    private String findEvidence(
            String text,
            String alias
    ) {
        String lowerText =
                text.toLowerCase(Locale.ROOT);

        int index =
                lowerText.indexOf(
                        alias.toLowerCase(Locale.ROOT)
                );

        if (index < 0) {
            return "Skill detected in resume text";
        }

        int start =
                Math.max(0, index - 80);

        int end =
                Math.min(
                        text.length(),
                        index + alias.length() + 80
                );

        return text.substring(start, end)
                .replaceAll("\\s+", " ")
                .trim();
    }

    private String toTitleCase(
            String value
    ) {
        String[] words =
                value.split("\\s+");

        return Arrays.stream(words)
                .filter(word -> !word.isBlank())
                .map(word ->
                        Character.toUpperCase(
                                word.charAt(0)
                        ) + word.substring(1)
                )
                .reduce(
                        (first, second) ->
                                first + " " + second
                )
                .orElse(value);
    }

    private static Map<String, String>
    createSkillMap() {
        Map<String, String> skills =
                new LinkedHashMap<>();

        skills.put("java", "Java");

        skills.put("javascript", "JavaScript");
        skills.put("js", "JavaScript");

        skills.put("python", "Python");

        skills.put("sql", "SQL");

        skills.put("html", "HTML");
        skills.put("css", "CSS");

        skills.put("react", "React");
        skills.put("reactjs", "React");

        skills.put("spring", "Spring");
        skills.put("spring boot", "Spring Boot");
        skills.put("springboot", "Spring Boot");
        skills.put("spring security", "Spring Security");

        skills.put("rest api", "REST API");
        skills.put("restful api", "REST API");

        skills.put("postgresql", "PostgreSQL");
        skills.put("postgres", "PostgreSQL");

        skills.put("mysql", "MySQL");
        skills.put("mongodb", "MongoDB");

        skills.put("docker", "Docker");

        skills.put("git", "Git");
        skills.put("github", "GitHub");

        skills.put("jwt", "JWT");

        skills.put(
                "machine learning",
                "Machine Learning"
        );

        skills.put(
                "deep learning",
                "Deep Learning"
        );

        skills.put(
                "data structures",
                "Data Structures"
        );

        skills.put(
                "algorithms",
                "Algorithms"
        );

        return Collections.unmodifiableMap(
                skills
        );
    }

    public record ExtractedSkill(
            String name,
            String source,
            Double confidence,
            String evidenceText
    ) {
    }
}