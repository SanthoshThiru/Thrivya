import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import DashboardCard from "../../components/DashboardCard";
import { getStudentDashboard } from "../../api/dashboardApi";
import { getStudentProfile } from "../../api/studentApi";

export default function StudentDashboard() {
  const navigate = useNavigate();

  const [dashboard, setDashboard] = useState(null);
  const [profile, setProfile] = useState(null);
  const [error, setError] = useState("");
  const [profileLoading, setProfileLoading] = useState(true);

  const storedUser = localStorage.getItem("thrivyaUser");

  let user = null;

  try {
    user = storedUser ? JSON.parse(storedUser) : null;
  } catch {
    user = null;
  }

  useEffect(() => {
    let isMounted = true;

    async function loadDashboardData() {
      setError("");
      setProfileLoading(true);

      try {
        const dashboardData = await getStudentDashboard();

        if (isMounted) {
          setDashboard(dashboardData);
        }
      } catch (requestError) {
        if (requestError.response?.status === 401) {
          localStorage.removeItem("thrivyaToken");
          localStorage.removeItem("thrivyaUser");

          navigate("/login", {
            replace: true,
          });

          return;
        }

        if (isMounted) {
          setError(
            requestError.response?.data?.message ||
              "Unable to load student dashboard"
          );
        }
      }

      try {
        const profileData = await getStudentProfile();

        if (isMounted) {
          setProfile(profileData);
        }
      } catch (requestError) {
        if (requestError.response?.status === 401) {
          localStorage.removeItem("thrivyaToken");
          localStorage.removeItem("thrivyaUser");

          navigate("/login", {
            replace: true,
          });

          return;
        }

        if (isMounted) {
          setProfile(null);
        }
      } finally {
        if (isMounted) {
          setProfileLoading(false);
        }
      }
    }

    loadDashboardData();

    return () => {
      isMounted = false;
    };
  }, [navigate]);

  const studentName =
    user?.username ||
    user?.userName ||
    user?.name ||
    "Student";

  const hasUploadedResume =
    Boolean(profile?.resumeId) ||
    Boolean(profile?.fileName);

  const hasSelectedRole =
    Boolean(profile?.selectedRoleId) ||
    Boolean(profile?.selectedRoleTitle);

  const hasSkills =
    Array.isArray(profile?.skills) &&
    profile.skills.length > 0;

  const canGenerateATS =
    hasUploadedResume &&
    hasSelectedRole &&
    !profileLoading;

  const canViewSkillGaps =
    hasSelectedRole &&
    hasSkills &&
    !profileLoading;

  const canGenerateResumeContent =
    hasUploadedResume &&
    hasSelectedRole &&
    hasSkills &&
    !profileLoading;

  let atsStatusMessage =
    "Upload a resume and select a target role to generate your ATS report.";

  if (profileLoading) {
    atsStatusMessage =
      "Checking your resume and selected role...";
  } else if (!hasUploadedResume && !hasSelectedRole) {
    atsStatusMessage =
      "Upload a resume and select a target role before generating ATS.";
  } else if (!hasUploadedResume) {
    atsStatusMessage =
      "Upload a resume before generating ATS.";
  } else if (!hasSelectedRole) {
    atsStatusMessage =
      "Select a target role before generating ATS.";
  } else {
    atsStatusMessage =
      "Your ATS report will use your latest resume and selected role.";
  }

  let skillGapStatusMessage =
    "Add skills and select a target role to view skill gaps.";

  if (profileLoading) {
    skillGapStatusMessage =
      "Checking your skill profile and selected role...";
  } else if (!hasSkills) {
    skillGapStatusMessage =
      "Add skills or upload a resume before viewing skill gaps.";
  } else if (!hasSelectedRole) {
    skillGapStatusMessage =
      "Select a target role before viewing skill gaps.";
  } else {
    skillGapStatusMessage =
      "View ordered skill gaps and recommended learning resources for your selected role.";
  }

  let resumeContentStatusMessage =
    "Upload a resume, add skills, and select a target role before generating resume content.";

  if (profileLoading) {
    resumeContentStatusMessage =
      "Checking your resume evidence and selected role...";
  } else if (!hasUploadedResume) {
    resumeContentStatusMessage =
      "Upload a resume before generating evidence-aware resume content.";
  } else if (!hasSkills) {
    resumeContentStatusMessage =
      "Add skills or upload a readable resume before generating resume content.";
  } else if (!hasSelectedRole) {
    resumeContentStatusMessage =
      "Select a target role before generating resume content.";
  } else {
    resumeContentStatusMessage =
      "Generate copy-ready content using your latest resume, selected role, verified skills, and learning evidence.";
  }

  function handleProfileSetup() {
    navigate("/student/setup");
  }

  function handleShowSkills() {
    navigate("/student/skills");
  }

  function handleATSReport() {
    if (!canGenerateATS) {
      return;
    }

    navigate("/student/ats");
  }

  function handleSkillGapAnalysis() {
    if (!canViewSkillGaps) {
      return;
    }

    navigate("/student/skill-gaps");
  }

  function handleResumeHistory() {
    navigate("/student/resume-versions");
  }

  function handleProgressUpdate() {
    navigate("/student/progress");
  }

  function handleResumeContent() {
    if (!canGenerateResumeContent) {
      return;
    }

    navigate("/student/resume-content");
  }

  function handleLogout() {
    localStorage.removeItem("thrivyaToken");
    localStorage.removeItem("thrivyaUser");

    navigate("/login", {
      replace: true,
    });
  }

  function handleTryAgain() {
    window.location.reload();
  }

  if (error) {
    return (
      <main style={styles.page}>
        <section style={styles.errorCard}>
          <header style={styles.header}>
            <div>
              <p style={styles.brand}>Thrivya</p>
              <h1>Student Dashboard</h1>
            </div>

            <button
              type="button"
              style={styles.logoutButton}
              onClick={handleLogout}
            >
              Logout
            </button>
          </header>

          <p style={styles.error}>{error}</p>

          <button
            type="button"
            onClick={handleTryAgain}
            style={styles.primaryButton}
          >
            Try again
          </button>
        </section>
      </main>
    );
  }

  if (!dashboard) {
    return (
      <main style={styles.page}>
        <section style={styles.loadingCard}>
          <p style={styles.brand}>Thrivya</p>
          <h1>Student Dashboard</h1>
          <p>Loading your career dashboard...</p>
        </section>
      </main>
    );
  }

  return (
    <DashboardCard
      title={dashboard.title || "Student Dashboard"}
      role={dashboard.role || "STUDENT"}
      email={dashboard.email || user?.email || "Student"}
      message={
        dashboard.message ||
        "Your Thrivya career journey starts here."
      }
    >
      <p style={styles.welcomeText}>
        Welcome, <strong>{studentName}</strong>
      </p>

      <section style={styles.infoBox}>
        <h2>Build your career profile</h2>

        <p>
          Upload or update your resume, enter skills manually,
          choose a desired role, or receive relevant role
          suggestions from Thrivya.
        </p>

        <div style={styles.buttonGroup}>
          <button
            type="button"
            onClick={handleProfileSetup}
            style={styles.primaryButton}
          >
            Set up my career profile
          </button>

          <button
            type="button"
            onClick={handleShowSkills}
            style={styles.secondaryButton}
          >
            Show my skills
          </button>
        </div>
      </section>

      <section style={styles.atsBox}>
        <div>
          <h2 style={styles.atsTitle}>
            Explainable ATS analysis
          </h2>

          <p style={styles.atsDescription}>
            Analyze your latest uploaded resume against your
            selected career role with factor-wise explanations.
          </p>

          <p
            style={
              canGenerateATS
                ? styles.readyMessage
                : styles.warningMessage
            }
          >
            {atsStatusMessage}
          </p>
        </div>

        <button
          type="button"
          onClick={handleATSReport}
          disabled={!canGenerateATS}
          style={
            canGenerateATS
              ? styles.atsButton
              : styles.disabledATSButton
          }
          title={
            canGenerateATS
              ? "Open explainable ATS report"
              : atsStatusMessage
          }
        >
          {profileLoading
            ? "Checking profile..."
            : "View ATS report"}
        </button>
      </section>

      <section style={styles.skillGapBox}>
        <div>
          <h2 style={styles.skillGapTitle}>
            Ordered skill-gap analysis
          </h2>

          <p style={styles.skillGapDescription}>
            Compare your current skills with your selected role
            and identify the most important gaps first.
          </p>

          <p
            style={
              canViewSkillGaps
                ? styles.readyMessage
                : styles.warningMessage
            }
          >
            {skillGapStatusMessage}
          </p>
        </div>

        <button
          type="button"
          onClick={handleSkillGapAnalysis}
          disabled={!canViewSkillGaps}
          style={
            canViewSkillGaps
              ? styles.skillGapButton
              : styles.disabledSkillGapButton
          }
          title={
            canViewSkillGaps
              ? "Open ordered skill-gap analysis"
              : skillGapStatusMessage
          }
        >
          View skill gaps
        </button>
      </section>

      <section style={styles.progressBox}>
        <div>
          <h2 style={styles.progressTitle}>
            Learning progress
          </h2>

          <p style={styles.progressDescription}>
            Record new learning progress and evidence. Your
            skill profile and future gap analysis can then
            reflect the update.
          </p>
        </div>

        <button
          type="button"
          onClick={handleProgressUpdate}
          style={styles.progressButton}
        >
          Update learning progress
        </button>
      </section>

      <section style={styles.resumeContentBox}>
        <div>
          <h2 style={styles.resumeContentTitle}>
            Evidence-aware resume content
          </h2>

          <p style={styles.resumeContentDescription}>
            Generate safe, copy-ready resume content using your
            latest resume, selected role, verified skills, and
            learning-progress evidence.
          </p>

          <p
            style={
              canGenerateResumeContent
                ? styles.readyMessage
                : styles.warningMessage
            }
          >
            {resumeContentStatusMessage}
          </p>
        </div>

        <button
          type="button"
          onClick={handleResumeContent}
          disabled={!canGenerateResumeContent}
          style={
            canGenerateResumeContent
              ? styles.resumeContentButton
              : styles.disabledResumeContentButton
          }
          title={
            canGenerateResumeContent
              ? "Open resume content generation"
              : resumeContentStatusMessage
          }
        >
          {profileLoading
            ? "Checking profile..."
            : "Generate resume content"}
        </button>
      </section>

      <section style={styles.resumeHistoryBox}>
        <div>
          <h2 style={styles.resumeHistoryTitle}>
            Resume version history
          </h2>

          <p style={styles.resumeHistoryDescription}>
            View your active resume, previous versions, and
            track improvement over time.
          </p>
        </div>

        <button
          type="button"
          onClick={handleResumeHistory}
          style={styles.resumeHistoryButton}
        >
          View resume history
        </button>
      </section>

      <section style={styles.statusBox}>
        <h2>Profile status</h2>

        <div style={styles.statusRow}>
          <span>Resume uploaded</span>

          <span
            style={
              hasUploadedResume
                ? styles.completedStatus
                : styles.pendingStatus
            }
          >
            {hasUploadedResume
              ? "Completed"
              : "Pending"}
          </span>
        </div>

        <div style={styles.statusRow}>
          <span>Skills available</span>

          <span
            style={
              hasSkills
                ? styles.completedStatus
                : styles.pendingStatus
            }
          >
            {hasSkills
              ? "Completed"
              : "Pending"}
          </span>
        </div>

        <div style={styles.statusRow}>
          <span>Target role selected</span>

          <span
            style={
              hasSelectedRole
                ? styles.completedStatus
                : styles.pendingStatus
            }
          >
            {hasSelectedRole
              ? "Completed"
              : "Pending"}
          </span>
        </div>

        {profile?.selectedRoleTitle && (
          <div style={styles.selectedRole}>
            Selected role:{" "}
            <strong>
              {profile.selectedRoleTitle}
            </strong>
          </div>
        )}
      </section>
    </DashboardCard>
  );
}

const styles = {
  page: {
    minHeight: "100vh",
    padding: "32px 20px",
    background: "#f4f7fb",
  },

  loadingCard: {
    width: "100%",
    maxWidth: "850px",
    margin: "0 auto",
    padding: "32px",
    borderRadius: "18px",
    background: "#ffffff",
    boxShadow:
      "0 10px 30px rgba(15, 23, 42, 0.12)",
  },

  errorCard: {
    width: "100%",
    maxWidth: "850px",
    margin: "0 auto",
    padding: "32px",
    borderRadius: "18px",
    background: "#ffffff",
    boxShadow:
      "0 10px 30px rgba(15, 23, 42, 0.12)",
  },

  header: {
    display: "flex",
    alignItems: "flex-start",
    justifyContent: "space-between",
    gap: "20px",
    marginBottom: "20px",
  },

  brand: {
    margin: "0 0 8px",
    color: "#2563eb",
    fontSize: "18px",
    fontWeight: 700,
  },

  welcomeText: {
    fontSize: "17px",
    color: "#334155",
  },

  logoutButton: {
    flexShrink: 0,
    padding: "10px 16px",
    border: "none",
    borderRadius: "8px",
    background: "#ef4444",
    color: "#ffffff",
    fontSize: "14px",
    fontWeight: 600,
    cursor: "pointer",
  },

  infoBox: {
    marginTop: "28px",
    padding: "24px",
    border: "1px solid #dbeafe",
    borderRadius: "14px",
    background: "#eff6ff",
  },

  buttonGroup: {
    display: "flex",
    flexWrap: "wrap",
    gap: "12px",
    marginTop: "8px",
  },

  primaryButton: {
    marginTop: "16px",
    padding: "12px 18px",
    border: "none",
    borderRadius: "9px",
    background: "#2563eb",
    color: "#ffffff",
    fontSize: "15px",
    fontWeight: 600,
    cursor: "pointer",
  },

  secondaryButton: {
    marginTop: "16px",
    padding: "12px 18px",
    border: "1px solid #2563eb",
    borderRadius: "9px",
    background: "#ffffff",
    color: "#2563eb",
    fontSize: "15px",
    fontWeight: 600,
    cursor: "pointer",
  },

  atsBox: {
    display: "flex",
    alignItems: "center",
    justifyContent: "space-between",
    gap: "24px",
    marginTop: "24px",
    padding: "24px",
    border: "1px solid #c7d2fe",
    borderRadius: "14px",
    background: "#eef2ff",
  },

  atsTitle: {
    marginTop: 0,
    marginBottom: "8px",
    color: "#3730a3",
  },

  atsDescription: {
    marginTop: 0,
    color: "#4338ca",
  },

  atsButton: {
    flexShrink: 0,
    padding: "12px 18px",
    border: "none",
    borderRadius: "9px",
    background: "#4f46e5",
    color: "#ffffff",
    fontSize: "15px",
    fontWeight: 600,
    cursor: "pointer",
  },

  disabledATSButton: {
    flexShrink: 0,
    padding: "12px 18px",
    border: "none",
    borderRadius: "9px",
    background: "#a5b4fc",
    color: "#ffffff",
    fontSize: "15px",
    fontWeight: 600,
    cursor: "not-allowed",
  },

  readyMessage: {
    marginBottom: 0,
    color: "#166534",
    fontSize: "14px",
  },

  warningMessage: {
    marginBottom: 0,
    color: "#92400e",
    fontSize: "14px",
  },

  skillGapBox: {
    display: "flex",
    alignItems: "center",
    justifyContent: "space-between",
    gap: "24px",
    marginTop: "24px",
    padding: "24px",
    border: "1px solid #a7f3d0",
    borderRadius: "14px",
    background: "#ecfdf5",
  },

  skillGapTitle: {
    marginTop: 0,
    marginBottom: "8px",
    color: "#047857",
  },

  skillGapDescription: {
    marginTop: 0,
    color: "#065f46",
  },

  skillGapButton: {
    flexShrink: 0,
    padding: "12px 18px",
    border: "none",
    borderRadius: "9px",
    background: "#059669",
    color: "#ffffff",
    fontSize: "15px",
    fontWeight: 600,
    cursor: "pointer",
  },

  disabledSkillGapButton: {
    flexShrink: 0,
    padding: "12px 18px",
    border: "none",
    borderRadius: "9px",
    background: "#6ee7b7",
    color: "#ffffff",
    fontSize: "15px",
    fontWeight: 600,
    cursor: "not-allowed",
  },

  progressBox: {
    display: "flex",
    alignItems: "center",
    justifyContent: "space-between",
    gap: "24px",
    marginTop: "24px",
    padding: "24px",
    border: "1px solid #ddd6fe",
    borderRadius: "14px",
    background: "#f5f3ff",
  },

  progressTitle: {
    marginTop: 0,
    marginBottom: "8px",
    color: "#6d28d9",
  },

  progressDescription: {
    marginTop: 0,
    color: "#5b21b6",
  },

  progressButton: {
    flexShrink: 0,
    padding: "12px 18px",
    border: "none",
    borderRadius: "9px",
    background: "#7c3aed",
    color: "#ffffff",
    fontSize: "15px",
    fontWeight: 600,
    cursor: "pointer",
  },

  resumeContentBox: {
    display: "flex",
    alignItems: "center",
    justifyContent: "space-between",
    gap: "24px",
    marginTop: "24px",
    padding: "24px",
    border: "1px solid #fde68a",
    borderRadius: "14px",
    background: "#fffbeb",
  },

  resumeContentTitle: {
    marginTop: 0,
    marginBottom: "8px",
    color: "#a16207",
  },

  resumeContentDescription: {
    marginTop: 0,
    color: "#854d0e",
  },

  resumeContentButton: {
    flexShrink: 0,
    padding: "12px 18px",
    border: "none",
    borderRadius: "9px",
    background: "#ca8a04",
    color: "#ffffff",
    fontSize: "15px",
    fontWeight: 600,
    cursor: "pointer",
  },

  disabledResumeContentButton: {
    flexShrink: 0,
    padding: "12px 18px",
    border: "none",
    borderRadius: "9px",
    background: "#fcd34d",
    color: "#ffffff",
    fontSize: "15px",
    fontWeight: 600,
    cursor: "not-allowed",
  },

  resumeHistoryBox: {
    display: "flex",
    alignItems: "center",
    justifyContent: "space-between",
    gap: "24px",
    marginTop: "24px",
    padding: "24px",
    border: "1px solid #bae6fd",
    borderRadius: "14px",
    background: "#f0f9ff",
  },

  resumeHistoryTitle: {
    marginTop: 0,
    marginBottom: "8px",
    color: "#0369a1",
  },

  resumeHistoryDescription: {
    marginTop: 0,
    color: "#075985",
  },

  resumeHistoryButton: {
    flexShrink: 0,
    padding: "12px 18px",
    border: "none",
    borderRadius: "9px",
    background: "#0284c7",
    color: "#ffffff",
    fontSize: "15px",
    fontWeight: 600,
    cursor: "pointer",
  },

  statusBox: {
    marginTop: "24px",
    padding: "24px",
    border: "1px solid #e2e8f0",
    borderRadius: "14px",
    background: "#f8fafc",
  },

  statusRow: {
    display: "flex",
    alignItems: "center",
    justifyContent: "space-between",
    gap: "16px",
    padding: "12px 0",
    borderBottom: "1px solid #e2e8f0",
  },

  completedStatus: {
    padding: "5px 10px",
    borderRadius: "20px",
    background: "#dcfce7",
    color: "#166534",
    fontSize: "13px",
    fontWeight: 600,
  },

  pendingStatus: {
    padding: "5px 10px",
    borderRadius: "20px",
    background: "#fef3c7",
    color: "#92400e",
    fontSize: "13px",
    fontWeight: 600,
  },

  selectedRole: {
    marginTop: "16px",
    color: "#334155",
  },

  error: {
    padding: "12px",
    borderRadius: "8px",
    background: "#fee2e2",
    color: "#b91c1c",
  },
};