package com.example.thrivya.repository;

import com.example.thrivya.model.ResumeContentDraft;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResumeContentDraftRepository
        extends JpaRepository<ResumeContentDraft, Long> {

    List<ResumeContentDraft>
    findAllByUserIdOrderByGeneratedAtDesc(Long userId);
}