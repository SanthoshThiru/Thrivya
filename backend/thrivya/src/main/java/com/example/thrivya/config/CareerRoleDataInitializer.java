package com.example.thrivya.config;

import com.example.thrivya.model.Role;
import com.example.thrivya.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class CareerRoleDataInitializer {

    private final RoleRepository roleRepository;

    @Bean
    public CommandLineRunner seedCareerRoles() {
        return args -> {
            List<RoleSeed> roles = List.of(
                    new RoleSeed(
                            "Java Backend Developer",
                            "Builds backend services using Java, REST APIs, databases, and Spring Boot.",
                            "Backend Development"
                    ),
                    new RoleSeed(
                            "Spring Boot Developer",
                            "Develops enterprise APIs and backend applications using Spring Boot.",
                            "Backend Development"
                    ),
                    new RoleSeed(
                            "Full Stack Developer",
                            "Builds frontend and backend applications using web technologies.",
                            "Software Development"
                    ),
                    new RoleSeed(
                            "Data Analyst",
                            "Uses SQL, spreadsheets, statistics, and visualization to analyze data.",
                            "Data and Analytics"
                    ),
                    new RoleSeed(
                            "Machine Learning Engineer",
                            "Builds and deploys machine-learning systems and data pipelines.",
                            "Artificial Intelligence"
                    ),
                    new RoleSeed(
                            "Frontend Developer",
                            "Builds user interfaces using HTML, CSS, JavaScript, and frontend frameworks.",
                            "Frontend Development"
                    ),
                    new RoleSeed(
                            "DevOps Engineer",
                            "Automates deployment, infrastructure, containers, and CI/CD workflows.",
                            "Cloud and DevOps"
                    )
            );

            for (RoleSeed roleSeed : roles) {
                if (roleRepository
                        .findByTitleIgnoreCase(roleSeed.title())
                        .isEmpty()) {

                    roleRepository.save(
                            Role.builder()
                                    .title(roleSeed.title())
                                    .description(roleSeed.description())
                                    .domain(roleSeed.domain())
                                    .build()
                    );
                }
            }
        };
    }

    private record RoleSeed(
            String title,
            String description,
            String domain
    ) {
    }
}