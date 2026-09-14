package com.example.thrivya.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "skills",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_skill_canonical_name",
                        columnNames = "canonical_name"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "canonical_name", nullable = false)
    private String canonicalName;

    @Column(name = "category")
    private String category;
}