package com.example.thrivya.repository;

import com.example.thrivya.model.ATSEvidence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ATSEvidenceRepository
        extends JpaRepository<ATSEvidence, Long> {

    List<ATSEvidence>
    findAllByAtsReportId(Long atsReportId);

    void deleteAllByAtsReportId(Long atsReportId);
}