package com.example.thrivya.repository;

import com.example.thrivya.model.SkillProgressUpdate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SkillProgressUpdateRepository
        extends JpaRepository<SkillProgressUpdate, Long> {

    List<SkillProgressUpdate>
    findAllByUserIdOrderByCreatedAtDesc(Long userId);

    List<SkillProgressUpdate>
    findAllByUserIdAndSkillIdOrderByCreatedAtDesc(
            Long userId,
            Long skillId
    );
}