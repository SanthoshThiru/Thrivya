package com.example.thrivya.repository;

import com.example.thrivya.model.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResumeRepository
        extends JpaRepository<Resume, Long> {

    List<Resume>
    findAllByUserIdOrderByVersionNumberDesc(
            Long userId
    );

    Optional<Resume>
    findFirstByUserIdAndActiveTrueOrderByVersionNumberDesc(
            Long userId
    );

    Optional<Resume>
    findFirstByUserIdOrderByVersionNumberDesc(
            Long userId
    );

    long countByUserId(Long userId);
}