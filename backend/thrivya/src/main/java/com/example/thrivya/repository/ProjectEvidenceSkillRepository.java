package com.example.thrivya.repository;

import com.example.thrivya.model.ProjectEvidenceSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectEvidenceSkillRepository
        extends JpaRepository<ProjectEvidenceSkill, Long> {

    List<ProjectEvidenceSkill>
    findAllByProjectEvidenceId(Long projectEvidenceId);
}