package com.example.thrivya.repository;

import com.example.thrivya.model.ProfileSkill;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProfileSkillRepository
        extends JpaRepository<ProfileSkill, Long> {

    @EntityGraph(attributePaths = {"skill"})
    @Query("""
            SELECT profileSkill
            FROM ProfileSkill profileSkill
            WHERE profileSkill.profile.id = :profileId
            """)
    List<ProfileSkill> findAllByProfileIdWithSkill(
            @Param("profileId") Long profileId
    );

    List<ProfileSkill> findAllByProfileId(Long profileId);

    void deleteAllByProfileId(Long profileId);
}