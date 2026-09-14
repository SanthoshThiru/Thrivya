package com.example.thrivya.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "role_suggestions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleSuggestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    private CareerProfile profile;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "relevance_score", nullable = false)
    private Double relevanceScore;

    @Column(name = "rank_number", nullable = false)
    private Integer rankNumber;

    @Column(nullable = false)
    private Boolean selected;
}