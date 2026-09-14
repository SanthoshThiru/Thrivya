package com.example.thrivya.repository;

import com.example.thrivya.model.ProfileSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfileSkillRepository
        extends JpaRepository<ProfileSkill, Long> {

    List<ProfileSkill> findAllByProfileId(Long profileId);

    void deleteAllByProfileId(Long profileId);
}