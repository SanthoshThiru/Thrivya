package com.example.thrivya.repository;

import com.example.thrivya.model.RoleSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleSkillRepository
        extends JpaRepository<RoleSkill, Long> {

    List<RoleSkill> findAllByRoleId(Long roleId);

    List<RoleSkill> findAllBySkillId(Long skillId);
}