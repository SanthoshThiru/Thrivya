package com.example.thrivya.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "profile_skills",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_profile_skill",
                        columnNames = {
                                "profile_id",
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
public class ProfileSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    private CareerProfile profile;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(name = "source", nullable = false)
    private String source;

    @Column(name = "confidence")
    private Double confidence;

    @Column(name = "evidence_text", columnDefinition = "TEXT")
    private String evidenceText;
}