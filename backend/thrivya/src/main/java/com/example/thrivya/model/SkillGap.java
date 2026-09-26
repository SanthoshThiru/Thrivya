package com.example.thrivya.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "skill_gaps",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_profile_role_skill_analysis",
                        columnNames = {
                                "profile_id",
                                "role_id",
                                "skill_id",
                                "analysis_version"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillGap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    private CareerProfile profile;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id")
    private Resume resume;

    @Column(name = "analysis_version", nullable = false)
    private Integer analysisVersion;

    @Column(name = "current_level", nullable = false)
    private Double currentLevel;

    @Column(name = "target_level", nullable = false)
    private Double targetLevel;

    @Column(name = "gap_percentage", nullable = false)
    private Double gapPercentage;

    @Column(name = "priority_level", nullable = false)
    private Integer priorityLevel;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "rationale", columnDefinition = "TEXT")
    private String rationale;

    @Column(name = "calculated_at", nullable = false)
    private LocalDateTime calculatedAt;

    @PrePersist
    public void beforeCreate() {
        if (calculatedAt == null) {
            calculatedAt = LocalDateTime.now();
        }
    }
}
