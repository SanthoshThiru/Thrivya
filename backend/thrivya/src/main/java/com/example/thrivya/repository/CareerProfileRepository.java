package com.example.thrivya.repository;

import com.example.thrivya.model.CareerProfile;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CareerProfileRepository
        extends JpaRepository<CareerProfile, Long> {

    Optional<CareerProfile> findByUserId(Long userId);

    @EntityGraph(attributePaths = {"selectedRole"})
    @Query("""
            SELECT cp
            FROM CareerProfile cp
            WHERE cp.user.id = :userId
            """)
    Optional<CareerProfile> findByUserIdWithSelectedRole(
            @Param("userId") Long userId
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT cp
            FROM CareerProfile cp
            LEFT JOIN FETCH cp.selectedRole
            WHERE cp.user.id = :userId
            """)
    Optional<CareerProfile> findByUserIdForSkillGapUpdate(
            @Param("userId") Long userId
    );
}