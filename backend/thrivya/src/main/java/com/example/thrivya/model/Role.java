package com.example.thrivya.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "career_roles",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_career_role_title",
                        columnNames = "title"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "domain_name")
    private String domain;
}