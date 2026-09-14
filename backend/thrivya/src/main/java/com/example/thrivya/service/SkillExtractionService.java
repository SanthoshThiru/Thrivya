package com.example.thrivya.service;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@Service
public class SkillExtractionService {

    private static final Map<String, String> CANONICAL_SKILLS =
            createSkillMap();

    public List<ExtractedSkill> extractFromManualText(
            String skillsText
    ) {
        if (skillsText == null || skillsText.isBlank()) {
            return List.of();
        }

        Set<String> detectedNames =
                new LinkedHashSet<>();

        String[] enteredParts =
                skillsText.split("[,;\\n|]");

        for (String part : enteredParts) {
            String normalized =
                    normalizeSkill(part);

            if (normalized != null) {
                detectedNames.add(normalized);
            }
        }

        return detectedNames.stream()
                .map(skill ->
                        new ExtractedSkill(
                                skill,
                                "MANUAL_INPUT",
                                1.0,
                                "Entered by the user"
                        )
                )
                .toList();
    }

    public List<ExtractedSkill> extractFromResumeText(
            String resumeText
    ) {
        if (resumeText == null || resumeText.isBlank()) {
            return List.of();
        }

        String lowerText =
                resumeText.toLowerCase(Locale.ROOT);

        List<ExtractedSkill> result =
                new ArrayList<>();

        for (Map.Entry<String, String> entry :
                CANONICAL_SKILLS.entrySet()) {

            String alias = entry.getKey();
            String canonicalName = entry.getValue();

            Pattern pattern =
                    Pattern.compile(
                            "(?<![a-z0-9])"
                                    + Pattern.quote(alias)
                                    + "(?![a-z0-9])",
                            Pattern.CASE_INSENSITIVE
                    );

            if (pattern.matcher(lowerText).find()) {
                result.add(
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
        }

        return result.stream()
                .filter(uniqueByName())
                .toList();
    }

    private String normalizeSkill(String rawSkill) {
        if (rawSkill == null || rawSkill.isBlank()) {
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

        return toTitleCase(rawSkill.trim());
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

    private Predicate<ExtractedSkill> uniqueByName() {
        Set<String> names = new HashSet<>();

        return skill ->
                names.add(
                        skill.name()
                                .toLowerCase(Locale.ROOT)
                );
    }

    private String toTitleCase(String value) {
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

    private static Map<String, String> createSkillMap() {
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

        return Collections.unmodifiableMap(skills);
    }

    public record ExtractedSkill(
            String name,
            String source,
            Double confidence,
            String evidenceText
    ) {
    }
}