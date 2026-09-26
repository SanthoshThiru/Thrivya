package com.example.thrivya.config;

import com.example.thrivya.model.Role;
import com.example.thrivya.model.RoleSkill;
import com.example.thrivya.model.Skill;
import com.example.thrivya.repository.RoleRepository;
import com.example.thrivya.repository.RoleSkillRepository;
import com.example.thrivya.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Configuration
@Order(2)
@RequiredArgsConstructor
public class RoleSkillDataInitializer {

    private final RoleRepository roleRepository;
    private final SkillRepository skillRepository;
    private final RoleSkillRepository roleSkillRepository;

    @Bean
    public CommandLineRunner seedRoleSkills() {
        return args -> seedAllRoleSkills();
    }

    @Transactional
    public void seedAllRoleSkills() {

        addRoleSkills(
                "Java Backend Developer",
                List.of(
                        new SkillSeed(
                                "Java",
                                1,
                                "FUNDAMENTAL",
                                1.0
                        ),
                        new SkillSeed(
                                "SQL",
                                2,
                                "FUNDAMENTAL",
                                0.9
                        ),
                        new SkillSeed(
                                "Spring Boot",
                                3,
                                "INTERMEDIATE",
                                1.0
                        ),
                        new SkillSeed(
                                "REST API",
                                4,
                                "INTERMEDIATE",
                                0.9
                        ),
                        new SkillSeed(
                                "Docker",
                                5,
                                "ADVANCED",
                                0.7
                        )
                )
        );

        addRoleSkills(
                "Spring Boot Developer",
                List.of(
                        new SkillSeed(
                                "Java",
                                1,
                                "FUNDAMENTAL",
                                1.0
                        ),
                        new SkillSeed(
                                "Spring Boot",
                                2,
                                "INTERMEDIATE",
                                1.0
                        ),
                        new SkillSeed(
                                "SQL",
                                3,
                                "FUNDAMENTAL",
                                0.8
                        ),
                        new SkillSeed(
                                "REST API",
                                4,
                                "INTERMEDIATE",
                                0.9
                        ),
                        new SkillSeed(
                                "Spring Security",
                                5,
                                "ADVANCED",
                                0.8
                        )
                )
        );

        addRoleSkills(
                "Full Stack Developer",
                List.of(
                        new SkillSeed(
                                "JavaScript",
                                1,
                                "FUNDAMENTAL",
                                0.9
                        ),
                        new SkillSeed(
                                "HTML",
                                2,
                                "FUNDAMENTAL",
                                0.8
                        ),
                        new SkillSeed(
                                "CSS",
                                3,
                                "FUNDAMENTAL",
                                0.8
                        ),
                        new SkillSeed(
                                "React",
                                4,
                                "INTERMEDIATE",
                                1.0
                        ),
                        new SkillSeed(
                                "Spring Boot",
                                5,
                                "ADVANCED",
                                0.7
                        )
                )
        );

        addRoleSkills(
                "Data Analyst",
                List.of(
                        new SkillSeed(
                                "SQL",
                                1,
                                "FUNDAMENTAL",
                                1.0
                        ),
                        new SkillSeed(
                                "Python",
                                2,
                                "INTERMEDIATE",
                                0.8
                        ),
                        new SkillSeed(
                                "Statistics",
                                3,
                                "INTERMEDIATE",
                                0.9
                        ),
                        new SkillSeed(
                                "Machine Learning",
                                4,
                                "ADVANCED",
                                0.6
                        )
                )
        );

        addRoleSkills(
                "Machine Learning Engineer",
                List.of(
                        new SkillSeed(
                                "Python",
                                1,
                                "FUNDAMENTAL",
                                1.0
                        ),
                        new SkillSeed(
                                "Machine Learning",
                                2,
                                "INTERMEDIATE",
                                1.0
                        ),
                        new SkillSeed(
                                "Deep Learning",
                                3,
                                "ADVANCED",
                                0.8
                        ),
                        new SkillSeed(
                                "SQL",
                                4,
                                "FUNDAMENTAL",
                                0.6
                        )
                )
        );

        addRoleSkills(
                "Frontend Developer",
                List.of(
                        new SkillSeed(
                                "HTML",
                                1,
                                "FUNDAMENTAL",
                                1.0
                        ),
                        new SkillSeed(
                                "CSS",
                                2,
                                "FUNDAMENTAL",
                                0.9
                        ),
                        new SkillSeed(
                                "JavaScript",
                                3,
                                "FUNDAMENTAL",
                                1.0
                        ),
                        new SkillSeed(
                                "React",
                                4,
                                "INTERMEDIATE",
                                1.0
                        )
                )
        );

        addRoleSkills(
                "DevOps Engineer",
                List.of(
                        new SkillSeed(
                                "Linux",
                                1,
                                "FUNDAMENTAL",
                                0.8
                        ),
                        new SkillSeed(
                                "Git",
                                2,
                                "FUNDAMENTAL",
                                0.8
                        ),
                        new SkillSeed(
                                "Docker",
                                3,
                                "INTERMEDIATE",
                                1.0
                        ),
                        new SkillSeed(
                                "CI/CD",
                                4,
                                "INTERMEDIATE",
                                0.9
                        )
                )
        );
    }

    private void addRoleSkills(
            String roleTitle,
            List<SkillSeed> skillSeeds
    ) {
        Role role =
                roleRepository
                        .findByTitleIgnoreCase(roleTitle)
                        .orElse(null);

        if (role == null) {
            System.out.println(
                    "Role not found while seeding skills: "
                            + roleTitle
            );
            return;
        }

        for (SkillSeed seed : skillSeeds) {
            Skill skill =
                    skillRepository
                            .findByCanonicalNameIgnoreCase(
                                    seed.name()
                            )
                            .orElseGet(() ->
                                    skillRepository.save(
                                            Skill.builder()
                                                    .canonicalName(
                                                            seed.name()
                                                    )
                                                    .build()
                                    )
                            );

            boolean alreadyExists =
                    roleSkillRepository
                            .existsByRoleIdAndSkillId(
                                    role.getId(),
                                    skill.getId()
                            );

            if (alreadyExists) {
                continue;
            }

            roleSkillRepository.save(
                    RoleSkill.builder()
                            .role(role)
                            .skill(skill)
                            .priorityLevel(
                                    seed.priority()
                            )
                            .requiredLevel(
                                    seed.requiredLevel()
                            )
                            .relevanceWeight(
                                    seed.weight()
                            )
                            .build()
            );
        }
    }

    private record SkillSeed(
            String name,
            Integer priority,
            String requiredLevel,
            Double weight
    ) {
    }
}