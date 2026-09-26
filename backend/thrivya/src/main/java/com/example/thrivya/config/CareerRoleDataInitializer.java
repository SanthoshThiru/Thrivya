package com.example.thrivya.config;

import com.example.thrivya.model.Role;
import com.example.thrivya.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.List;

@Configuration
@Order(1)
@RequiredArgsConstructor
public class CareerRoleDataInitializer {

    private final RoleRepository roleRepository;

    @Bean
    public CommandLineRunner seedCareerRoles() {
        return args -> {
            List<RoleSeed> roles = List.of(
                    new RoleSeed(
                            "Java Backend Developer",
                            "Builds backend services using Java, databases, REST APIs, and Spring Boot.",
                            "Backend Development"
                    ),
                    new RoleSeed(
                            "Spring Boot Developer",
                            "Develops backend APIs and enterprise applications using Spring Boot.",
                            "Backend Development"
                    ),
                    new RoleSeed(
                            "Full Stack Developer",
                            "Builds frontend and backend applications.",
                            "Software Development"
                    ),
                    new RoleSeed(
                            "Data Analyst",
                            "Analyzes data using SQL, statistics, and visualization.",
                            "Data and Analytics"
                    ),
                    new RoleSeed(
                            "Machine Learning Engineer",
                            "Builds and deploys machine-learning systems.",
                            "Artificial Intelligence"
                    ),
                    new RoleSeed(
                            "Frontend Developer",
                            "Builds user interfaces using web technologies.",
                            "Frontend Development"
                    ),
                    new RoleSeed(
                            "DevOps Engineer",
                            "Automates deployment, infrastructure, containers, and CI/CD.",
                            "Cloud and DevOps"
                    )
            );

            for (RoleSeed roleSeed : roles) {
                boolean exists =
                        roleRepository
                                .findByTitleIgnoreCase(
                                        roleSeed.title()
                                )
                                .isPresent();

                if (exists) {
                    continue;
                }

                roleRepository.save(
                        Role.builder()
                                .title(roleSeed.title())
                                .description(
                                        roleSeed.description()
                                )
                                .domain(
                                        roleSeed.domain()
                                )
                                .build()
                );
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