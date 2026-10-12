package com.example.thrivya.repository;

import com.example.thrivya.model.ProjectEvidence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectEvidenceRepository
        extends JpaRepository<ProjectEvidence, Long> {

    List<ProjectEvidence>
    findAllByUserIdOrderByCreatedAtDesc(Long userId);
}