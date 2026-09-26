package com.example.thrivya.repository;

import com.example.thrivya.model.RoleSkill;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoleSkillRepository
        extends JpaRepository<RoleSkill, Long> {

    boolean existsByRoleIdAndSkillId(
            Long roleId,
            Long skillId
    );

    @EntityGraph(attributePaths = {"skill", "role"})
    @Query("""
            SELECT roleSkill
            FROM RoleSkill roleSkill
            WHERE roleSkill.role.id = :roleId
            ORDER BY roleSkill.priorityLevel ASC
            """)
    List<RoleSkill> findAllByRoleIdWithSkillAndRole(
            @Param("roleId") Long roleId
    );

    List<RoleSkill> findAllByRoleId(Long roleId);

    List<RoleSkill> findAllByRoleIdOrderByPriorityLevelAsc(
            Long roleId
    );

    List<RoleSkill> findAllBySkillId(Long skillId);

    List<RoleSkill> findAllByRoleId(
            Long roleId,
            Sort sort
    );
}