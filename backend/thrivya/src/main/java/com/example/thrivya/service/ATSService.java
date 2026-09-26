package com.example.thrivya.service;

import com.example.thrivya.dto.response.ATSEvidenceResponse;
import com.example.thrivya.dto.response.ATSFactorResponse;
import com.example.thrivya.dto.response.ATSHistoryItemResponse;
import com.example.thrivya.dto.response.ATSReportResponse;
import com.example.thrivya.model.ATSEvidence;
import com.example.thrivya.model.ATSReport;
import com.example.thrivya.model.CareerProfile;
import com.example.thrivya.model.Resume;
import com.example.thrivya.model.Role;
import com.example.thrivya.model.RoleSkill;
import com.example.thrivya.model.User;
import com.example.thrivya.repository.ATSEvidenceRepository;
import com.example.thrivya.repository.ATSReportRepository;
import com.example.thrivya.repository.CareerProfileRepository;
import com.example.thrivya.repository.ResumeRepository;
import com.example.thrivya.repository.RoleSkillRepository;
import com.example.thrivya.repository.UserRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ATSService {

    private final UserRepository userRepository;

    private final CareerProfileRepository profileRepository;

    private final ResumeRepository resumeRepository;

    private final RoleSkillRepository roleSkillRepository;

    private final ATSReportRepository atsReportRepository;

    private final ATSEvidenceRepository atsEvidenceRepository;

    /**
     * Generates a new ATS report using the authenticated
     * student's selected role and active resume.
     */
    @Transactional
    public ATSReportResponse generateReport(
            String email
    ) {
        User user = getUser(email);

        CareerProfile profile =
                profileRepository.findByUserId(user.getId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Career profile not found"
                                ));

        Role selectedRole =
                profile.getSelectedRole();

        if (selectedRole == null) {
            throw new IllegalArgumentException(
                    "Please select a career role first"
            );
        }

        Long selectedRoleId =
                selectedRole.getId();

        String selectedRoleTitle =
                selectedRole.getTitle();

        Resume activeResume =
                resumeRepository
                        .findFirstByUserIdAndActiveTrueOrderByVersionNumberDesc(
                                user.getId()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Please upload a resume first"
                                ));

        Long resumeId =
                activeResume.getId();

        Integer resumeVersion =
                activeResume.getVersionNumber();

        String resumeText =
                Optional.ofNullable(
                        activeResume.getExtractedText()
                ).orElse("");

        List<RoleSkill> roleSkills =
                roleSkillRepository.findAllByRoleId(
                        selectedRoleId
                );

        if (roleSkills.isEmpty()) {
            throw new IllegalArgumentException(
                    "No required skills are configured for "
                            + selectedRoleTitle
            );
        }

        ATSCalculation calculation =
                calculateScores(
                        resumeText,
                        selectedRole,
                        roleSkills
                );

        ATSReport report =
                ATSReport.builder()
                        .resume(activeResume)
                        .role(selectedRole)
                        .overallScore(
                                calculation.overallScore()
                        )
                        .skillCoverageScore(
                                calculation.skillCoverageScore()
                        )
                        .evidenceScore(
                                calculation.evidenceScore()
                        )
                        .projectScore(
                                calculation.projectScore()
                        )
                        .sectionScore(
                                calculation.sectionScore()
                        )
                        .keywordScore(
                                calculation.keywordScore()
                        )
                        .build();

        ATSReport savedReport =
                atsReportRepository.save(report);

        saveEvidence(
                savedReport,
                calculation.evidence()
        );

        return buildResponse(
                savedReport,
                resumeId,
                resumeVersion,
                selectedRoleId,
                selectedRoleTitle,
                calculation
        );
    }

    /**
     * Gets the latest ATS report for the student's active
     * resume and selected role.
     */
    @Transactional(readOnly = true)
    public ATSReportResponse getLatestReport(
            String email
    ) {
        User user = getUser(email);

        CareerProfile profile =
                profileRepository.findByUserId(user.getId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Career profile not found"
                                ));

        Role selectedRole =
                profile.getSelectedRole();

        if (selectedRole == null) {
            throw new IllegalArgumentException(
                    "No selected role found"
            );
        }

        Long selectedRoleId =
                selectedRole.getId();

        String selectedRoleTitle =
                selectedRole.getTitle();

        Resume activeResume =
                resumeRepository
                        .findFirstByUserIdAndActiveTrueOrderByVersionNumberDesc(
                                user.getId()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "No active uploaded resume found"
                                ));

        Long activeResumeId =
                activeResume.getId();

        Integer activeResumeVersion =
                activeResume.getVersionNumber();

        ATSReport report =
                atsReportRepository
                        .findFirstByResumeIdAndRoleIdOrderByGeneratedAtDesc(
                                activeResumeId,
                                selectedRoleId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "No ATS report found for the active resume"
                                ));

        List<ATSEvidenceResponse> evidence =
                atsEvidenceRepository
                        .findAllByAtsReportId(report.getId())
                        .stream()
                        .map(this::toEvidenceResponse)
                        .toList();

        List<ATSFactorResponse> factors =
                buildStoredFactors(
                        report,
                        evidence
                );

        return ATSReportResponse.builder()
                .reportId(report.getId())
                .resumeId(activeResumeId)
                .resumeVersion(activeResumeVersion)
                .roleId(selectedRoleId)
                .roleTitle(selectedRoleTitle)
                .overallScore(report.getOverallScore())
                .factors(factors)
                .summary(
                        createSummary(
                                report.getOverallScore()
                        )
                )
                .build();
    }

    /**
     * Returns all ATS reports generated by the authenticated
     * student, newest first.
     */
    @Transactional(readOnly = true)
    public List<ATSHistoryItemResponse> getHistory(
            String email
    ) {
        User user = getUser(email);

        List<ATSReport> reports =
                atsReportRepository
                        .findAllByResumeUserIdOrderByGeneratedAtDesc(
                                user.getId()
                        );

        List<ATSHistoryItemResponse> history =
                new ArrayList<>();

        for (ATSReport report : reports) {
            Resume resume =
                    report.getResume();

            Role role =
                    report.getRole();

            history.add(
                    ATSHistoryItemResponse.builder()
                            .reportId(report.getId())
                            .resumeId(resume.getId())
                            .resumeVersion(
                                    resume.getVersionNumber()
                            )
                            .roleId(role.getId())
                            .roleTitle(role.getTitle())
                            .overallScore(
                                    report.getOverallScore()
                            )
                            .generatedAt(
                                    report.getGeneratedAt()
                            )
                            .build()
            );
        }

        return history;
    }

    /**
     * Calculates all ATS factors for one resume and one role.
     */
    private ATSCalculation calculateScores(
            String resumeText,
            Role role,
            List<RoleSkill> roleSkills
    ) {
        String lowerResume =
                resumeText.toLowerCase(Locale.ROOT);

        List<ATSEvidenceData> evidence =
                new ArrayList<>();

        int matchedSkills = 0;

        for (RoleSkill roleSkill : roleSkills) {
            String skillName =
                    roleSkill.getSkill()
                            .getCanonicalName();

            boolean found =
                    containsTerm(
                            lowerResume,
                            skillName
                    );

            if (found) {
                matchedSkills++;

                evidence.add(
                        ATSEvidenceData.builder()
                                .factorName(
                                        "Skill Coverage"
                                )
                                .itemName(skillName)
                                .status("FOUND")
                                .evidenceText(
                                        findEvidence(
                                                resumeText,
                                                skillName
                                        )
                                )
                                .recommendation(
                                        "Keep this skill and add measurable project outcomes."
                                )
                                .build()
                );
            } else {
                evidence.add(
                        ATSEvidenceData.builder()
                                .factorName(
                                        "Skill Coverage"
                                )
                                .itemName(skillName)
                                .status("MISSING")
                                .evidenceText(null)
                                .recommendation(
                                        "Add evidence through a project, experience, or verified learning activity."
                                )
                                .build()
                );
            }
        }

        double skillCoverageScore =
                percentage(
                        matchedSkills,
                        roleSkills.size()
                );

        double evidenceScore =
                calculateEvidenceScore(
                        resumeText,
                        evidence
                );

        double projectScore =
                calculateProjectScore(
                        lowerResume
                );

        double sectionScore =
                calculateSectionScore(
                        lowerResume
                );

        double keywordScore =
                calculateKeywordScore(
                        lowerResume,
                        role
                );

        double overallScore =
                round(
                        skillCoverageScore * 0.35
                                + evidenceScore * 0.25
                                + projectScore * 0.15
                                + sectionScore * 0.15
                                + keywordScore * 0.10
                );

        return new ATSCalculation(
                overallScore,
                skillCoverageScore,
                evidenceScore,
                projectScore,
                sectionScore,
                keywordScore,
                evidence
        );
    }

    /**
     * Calculates evidence strength.
     */
    private double calculateEvidenceScore(
            String resumeText,
            List<ATSEvidenceData> evidence
    ) {
        if (resumeText.isBlank()
                || evidence.isEmpty()) {
            return 0.0;
        }

        long found =
                evidence.stream()
                        .filter(item ->
                                "FOUND".equals(
                                        item.status()
                                )
                        )
                        .count();

        double baseScore =
                ((double) found / evidence.size())
                        * 70.0;

        double bonus = 0.0;

        String lowerText =
                resumeText.toLowerCase(Locale.ROOT);

        if (containsAny(
                lowerText,
                "experience",
                "internship",
                "employment",
                "worked",
                "developed"
        )) {
            bonus += 15.0;
        }

        if (containsAny(
                lowerText,
                "project",
                "built",
                "implemented",
                "developed"
        )) {
            bonus += 15.0;
        }

        return Math.min(
                100.0,
                baseScore + bonus
        );
    }

    /**
     * Calculates project evidence.
     */
    private double calculateProjectScore(
            String lowerResume
    ) {
        if (!lowerResume.contains("project")) {
            return 30.0;
        }

        return containsAny(
                lowerResume,
                "built",
                "developed",
                "implemented",
                "designed",
                "deployed"
        ) ? 90.0 : 65.0;
    }

    /**
     * Calculates section completeness.
     */
    private double calculateSectionScore(
            String lowerResume
    ) {
        List<String> sections =
                List.of(
                        "education",
                        "experience",
                        "project",
                        "skills"
                );

        long foundSections =
                sections.stream()
                        .filter(lowerResume::contains)
                        .count();

        return percentage(
                (int) foundSections,
                sections.size()
        );
    }

    /**
     * Calculates title-keyword alignment.
     */
    private double calculateKeywordScore(
            String lowerResume,
            Role role
    ) {
        String[] titleWords =
                role.getTitle()
                        .toLowerCase(Locale.ROOT)
                        .split("\\s+");

        int matchedWords = 0;

        for (String word : titleWords) {
            if (word.length() >= 3
                    && lowerResume.contains(word)) {
                matchedWords++;
            }
        }

        return percentage(
                matchedWords,
                titleWords.length
        );
    }

    /**
     * Stores evidence rows for the generated report.
     */
    private void saveEvidence(
            ATSReport report,
            List<ATSEvidenceData> evidence
    ) {
        List<ATSEvidence> entities =
                evidence.stream()
                        .map(item ->
                                ATSEvidence.builder()
                                        .atsReport(report)
                                        .factorName(
                                                item.factorName()
                                        )
                                        .itemName(
                                                item.itemName()
                                        )
                                        .status(
                                                item.status()
                                        )
                                        .evidenceText(
                                                item.evidenceText()
                                        )
                                        .recommendation(
                                                item.recommendation()
                                        )
                                        .build()
                        )
                        .toList();

        atsEvidenceRepository.saveAll(entities);
    }

    /**
     * Builds the response for a newly generated report.
     */
    private ATSReportResponse buildResponse(
            ATSReport report,
            Long resumeId,
            Integer resumeVersion,
            Long roleId,
            String roleTitle,
            ATSCalculation calculation
    ) {
        List<ATSEvidenceResponse> evidence =
                calculation.evidence()
                        .stream()
                        .map(this::toEvidenceResponse)
                        .toList();

        List<ATSFactorResponse> factors =
                List.of(
                        buildFactor(
                                "Skill Coverage",
                                calculation.skillCoverageScore(),
                                0.35,
                                evidenceFor(
                                        evidence,
                                        "Skill Coverage"
                                )
                        ),
                        buildFactor(
                                "Evidence Strength",
                                calculation.evidenceScore(),
                                0.25,
                                evidenceFor(
                                        evidence,
                                        "Skill Coverage"
                                )
                        ),
                        buildFactor(
                                "Project Evidence",
                                calculation.projectScore(),
                                0.15,
                                evidenceFor(
                                        evidence,
                                        "Project Evidence"
                                )
                        ),
                        buildFactor(
                                "Section Completeness",
                                calculation.sectionScore(),
                                0.15,
                                List.of()
                        ),
                        buildFactor(
                                "Keyword Alignment",
                                calculation.keywordScore(),
                                0.10,
                                List.of()
                        )
                );

        return ATSReportResponse.builder()
                .reportId(report.getId())
                .resumeId(resumeId)
                .resumeVersion(resumeVersion)
                .roleId(roleId)
                .roleTitle(roleTitle)
                .overallScore(
                        calculation.overallScore()
                )
                .factors(factors)
                .summary(
                        createSummary(
                                calculation.overallScore()
                        )
                )
                .build();
    }

    /**
     * Builds factor cards for an already stored report.
     */
    private List<ATSFactorResponse> buildStoredFactors(
            ATSReport report,
            List<ATSEvidenceResponse> evidence
    ) {
        return List.of(
                buildFactor(
                        "Skill Coverage",
                        report.getSkillCoverageScore(),
                        0.35,
                        evidenceFor(
                                evidence,
                                "Skill Coverage"
                        )
                ),
                buildFactor(
                        "Evidence Strength",
                        report.getEvidenceScore(),
                        0.25,
                        evidenceFor(
                                evidence,
                                "Skill Coverage"
                        )
                ),
                buildFactor(
                        "Project Evidence",
                        report.getProjectScore(),
                        0.15,
                        evidenceFor(
                                evidence,
                                "Project Evidence"
                        )
                ),
                buildFactor(
                        "Section Completeness",
                        report.getSectionScore(),
                        0.15,
                        List.of()
                ),
                buildFactor(
                        "Keyword Alignment",
                        report.getKeywordScore(),
                        0.10,
                        List.of()
                )
        );
    }

    /**
     * Creates one factor response.
     */
    private ATSFactorResponse buildFactor(
            String factorName,
            double score,
            double weight,
            List<ATSEvidenceResponse> evidence
    ) {
        return ATSFactorResponse.builder()
                .factorName(factorName)
                .score(score)
                .weight(weight)
                .evidence(evidence)
                .build();
    }

    /**
     * Filters evidence by factor.
     */
    private List<ATSEvidenceResponse> evidenceFor(
            List<ATSEvidenceResponse> evidence,
            String factorName
    ) {
        return evidence.stream()
                .filter(item ->
                        item.getFactorName() != null
                )
                .filter(item ->
                        item.getFactorName()
                                .equalsIgnoreCase(
                                        factorName
                                )
                )
                .toList();
    }

    /**
     * Converts a stored evidence entity to a DTO.
     */
    private ATSEvidenceResponse toEvidenceResponse(
            ATSEvidence item
    ) {
        return ATSEvidenceResponse.builder()
                .factorName(item.getFactorName())
                .itemName(item.getItemName())
                .status(item.getStatus())
                .evidenceText(item.getEvidenceText())
                .recommendation(
                        item.getRecommendation()
                )
                .build();
    }

    /**
     * Converts newly calculated evidence to a DTO.
     */
    private ATSEvidenceResponse toEvidenceResponse(
            ATSEvidenceData item
    ) {
        return ATSEvidenceResponse.builder()
                .factorName(item.factorName())
                .itemName(item.itemName())
                .status(item.status())
                .evidenceText(item.evidenceText())
                .recommendation(
                        item.recommendation()
                )
                .build();
    }

    /**
     * Creates a user-facing summary.
     */
    private String createSummary(
            double overallScore
    ) {
        if (overallScore >= 80) {
            return "Your resume has strong alignment with the selected role.";
        }

        if (overallScore >= 60) {
            return "Your resume has moderate alignment. Strengthen missing skills and evidence.";
        }

        return "Your resume needs improvement for the selected role. Focus on missing skills and evidence.";
    }

    /**
     * Checks whether a term appears as a separate word or phrase.
     */
    private boolean containsTerm(
            String lowerText,
            String term
    ) {
        String lowerTerm =
                term.toLowerCase(Locale.ROOT);

        return Pattern.compile(
                        "(?<![a-z0-9])"
                                + Pattern.quote(lowerTerm)
                                + "(?![a-z0-9])"
                )
                .matcher(lowerText)
                .find();
    }

    /**
     * Checks whether text contains at least one term.
     */
    private boolean containsAny(
            String text,
            String... terms
    ) {
        for (String term : terms) {
            if (text.contains(term)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Extracts a short evidence window around a matched skill.
     */
    private String findEvidence(
            String text,
            String term
    ) {
        String lowerText =
                text.toLowerCase(Locale.ROOT);

        int index =
                lowerText.indexOf(
                        term.toLowerCase(Locale.ROOT)
                );

        if (index < 0) {
            return null;
        }

        int start =
                Math.max(0, index - 80);

        int end =
                Math.min(
                        text.length(),
                        index + term.length() + 100
                );

        return text.substring(start, end)
                .replaceAll("\\s+", " ")
                .trim();
    }

    /**
     * Calculates a percentage from two integers.
     */
    private double percentage(
            int numerator,
            int denominator
    ) {
        if (denominator == 0) {
            return 0.0;
        }

        return round(
                ((double) numerator / denominator)
                        * 100.0
        );
    }

    /**
     * Rounds to two decimal places.
     */
    private double round(double value) {
        return Math.round(value * 100.0)
                / 100.0;
    }

    /**
     * Loads the authenticated user.
     */
    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Authenticated user not found"
                        ));
    }

    private record ATSCalculation(
            double overallScore,
            double skillCoverageScore,
            double evidenceScore,
            double projectScore,
            double sectionScore,
            double keywordScore,
            List<ATSEvidenceData> evidence
    ) {
    }

    @Builder
    private record ATSEvidenceData(
            String factorName,
            String itemName,
            String status,
            String evidenceText,
            String recommendation
    ) {
    }
}