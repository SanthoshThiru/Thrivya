package com.example.thrivya.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "role_skills",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_role_skill",
                        columnNames = {
                                "role_id",
                                "skill_id"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(name = "priority_level", nullable = false)
    private Integer priorityLevel;

    @Column(name = "required_level")
    private String requiredLevel;

    @Column(name = "relevance_weight", nullable = false)
    private Double relevanceWeight;
}