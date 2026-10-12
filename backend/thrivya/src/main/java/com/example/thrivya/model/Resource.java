package com.example.thrivya.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "resources",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_resource_skill_type_url",
                        columnNames = {
                                "skill_id",
                                "type",
                                "url"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecommendationType type;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String url;

    @Column(nullable = false)
    private String provider;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String difficulty;

    @Column(name = "estimated_minutes")
    private Integer estimatedMinutes;

    @Column(nullable = false)
    private Boolean verified;

    @Column(name = "last_verified_at")
    private LocalDateTime lastVerifiedAt;

    @Column(nullable = false)
    private Boolean active;

    @PrePersist
    public void beforeCreate() {
        if (verified == null) {
            verified = false;
        }

        if (active == null) {
            active = true;
        }
    }
}