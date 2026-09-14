package com.example.thrivya.repository;

import com.example.thrivya.model.RoleSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleSuggestionRepository
        extends JpaRepository<RoleSuggestion, Long> {

    List<RoleSuggestion> findAllByProfileIdOrderByRankNumberAsc(
            Long profileId
    );

    void deleteAllByProfileId(Long profileId);
}