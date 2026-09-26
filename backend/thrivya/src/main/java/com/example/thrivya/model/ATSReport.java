package com.example.thrivya.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ats_reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ATSReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "overall_score", nullable = false)
    private Double overallScore;

    @Column(name = "skill_coverage_score", nullable = false)
    private Double skillCoverageScore;

    @Column(name = "evidence_score", nullable = false)
    private Double evidenceScore;

    @Column(name = "project_score", nullable = false)
    private Double projectScore;

    @Column(name = "section_score", nullable = false)
    private Double sectionScore;

    @Column(name = "keyword_score", nullable = false)
    private Double keywordScore;

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;

    @PrePersist
    public void beforeCreate() {
        if (generatedAt == null) {
            generatedAt = LocalDateTime.now();
        }
    }
}