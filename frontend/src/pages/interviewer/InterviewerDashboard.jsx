import { useEffect, useState } from "react";

import DashboardCard from "../../components/DashboardCard";
import { getInterviewerDashboard } from "../../api/dashboardApi";

export default function InterviewerDashboard() {
  const [dashboard, setDashboard] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    async function loadDashboard() {
      try {
        const data = await getInterviewerDashboard();
        setDashboard(data);
      } catch (requestError) {
        setError(
          requestError.response?.data?.message ||
            "Unable to load interviewer dashboard"
        );
      }
    }

    loadDashboard();
  }, []);

  if (error) {
    return (
      <DashboardCard
        title="Technical Interviewer Dashboard"
        role="TECH_INTERVIEWER"
        email="Unknown user"
        message="Unable to load interviewer dashboard"
      >
        <p style={styles.error}>{error}</p>

        <button
          type="button"
          style={styles.primaryButton}
          onClick={() => window.location.reload()}
        >
          Try again
        </button>
      </DashboardCard>
    );
  }

  if (!dashboard) {
    return (
      <main style={styles.loadingPage}>
        <p>Loading interviewer dashboard...</p>
      </main>
    );
  }

  return (
    <DashboardCard
      title={
        dashboard.title ||
        "Technical Interviewer Dashboard"
      }
      role={dashboard.role || "TECH_INTERVIEWER"}
      email={dashboard.email || "Technical Interviewer"}
      message={
        dashboard.message ||
        "Review candidate skills, generate questions, and record structured feedback."
      }
    >
      <h2>Technical interview workspace</h2>

      <p>
        Review candidate skills, generate questions, and
        record structured feedback.
      </p>

      <div style={styles.actions}>
        <button
          type="button"
          style={styles.primaryButton}
          onClick={() => {
            alert(
              "Candidate profiles will be implemented next."
            );
          }}
        >
          View Candidate Profiles
        </button>

        <button
          type="button"
          style={styles.secondaryButton}
          onClick={() => {
            alert(
              "Question generation will be implemented next."
            );
          }}
        >
          Generate Questions
        </button>
      </div>
    </DashboardCard>
  );
}

const styles = {
  loadingPage: {
    minHeight: "100vh",
    display: "grid",
    placeItems: "center",
    background: "#f4f7fb",
  },

  actions: {
    display: "flex",
    flexWrap: "wrap",
    gap: "12px",
    marginTop: "20px",
  },

  primaryButton: {
    padding: "10px 18px",
    border: "none",
    borderRadius: "8px",
    backgroundColor: "#2563eb",
    color: "#ffffff",
    fontSize: "15px",
    fontWeight: 600,
    cursor: "pointer",
  },

  secondaryButton: {
    padding: "10px 18px",
    border: "none",
    borderRadius: "8px",
    backgroundColor: "#ea580c",
    color: "#ffffff",
    fontSize: "15px",
    fontWeight: 600,
    cursor: "pointer",
  },

  error: {
    marginBottom: "20px",
    padding: "12px",
    borderRadius: "8px",
    backgroundColor: "#fee2e2",
    color: "#b91c1c",
  },
};