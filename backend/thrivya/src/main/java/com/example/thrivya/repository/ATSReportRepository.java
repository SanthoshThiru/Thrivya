package com.example.thrivya.repository;

import com.example.thrivya.model.ATSReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ATSReportRepository
        extends JpaRepository<ATSReport, Long> {

    Optional<ATSReport>
    findFirstByResumeIdAndRoleIdOrderByGeneratedAtDesc(
            Long resumeId,
            Long roleId
    );

    List<ATSReport>
    findAllByResumeUserIdOrderByGeneratedAtDesc(
            Long userId
    );

    List<ATSReport>
    findAllByResumeIdOrderByGeneratedAtDesc(
            Long resumeId
    );

    List<ATSReport>
    findAllByRoleIdOrderByGeneratedAtDesc(
            Long roleId
    );
}