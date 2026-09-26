package com.example.thrivya.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ats_evidence")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ATSEvidence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ats_report_id", nullable = false)
    private ATSReport atsReport;

    @Column(name = "factor_name", nullable = false)
    private String factorName;

    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "evidence_text", columnDefinition = "TEXT")
    private String evidenceText;

    @Column(name = "recommendation", columnDefinition = "TEXT")
    private String recommendation;
}