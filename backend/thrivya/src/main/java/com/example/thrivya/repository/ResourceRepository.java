package com.example.thrivya.repository;

import com.example.thrivya.model.RecommendationType;
import com.example.thrivya.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResourceRepository
        extends JpaRepository<Resource, Long> {

    List<Resource>
    findAllBySkillIdAndTypeAndActiveTrueOrderByVerifiedDescIdAsc(
            Long skillId,
            RecommendationType type
    );

    List<Resource>
    findAllBySkillCanonicalNameIgnoreCaseAndTypeAndActiveTrueOrderByVerifiedDescIdAsc(
            String skillName,
            RecommendationType type
    );
}