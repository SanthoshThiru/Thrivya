import { useEffect, useState } from "react";

import DashboardCard from "../../components/DashboardCard";
import { getRecruiterDashboard } from "../../api/dashboardApi";

export default function RecruiterDashboard() {
  const [dashboard, setDashboard] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    async function loadDashboard() {
      try {
        const data = await getRecruiterDashboard();
        setDashboard(data);
      } catch (requestError) {
        setError(
          requestError.response?.data?.message ||
            "Unable to load recruiter dashboard"
        );
      }
    }

    loadDashboard();
  }, []);

  if (error) {
    return (
      <DashboardCard
        title="Recruiter Dashboard"
        role="RECRUITER"
        email="Unknown user"
        message="Unable to load recruiter dashboard"
      >
        <p style={styles.errorMessage}>{error}</p>

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
        <p>Loading recruiter dashboard...</p>
      </main>
    );
  }

  return (
    <DashboardCard
      title={dashboard.title || "Recruiter Dashboard"}
      role={dashboard.role || "RECRUITER"}
      email={dashboard.email || "Recruiter"}
      message={
        dashboard.message ||
        "Recruiter dashboard accessed"
      }
    >
      <h2>Recruiter workspace</h2>

      <p>
        Create job descriptions and discover candidates
        with verified skills and evidence.
      </p>

      <div style={styles.actions}>
        <button
          type="button"
          style={styles.primaryButton}
          onClick={() => {
            console.log("Create Job Description clicked");
          }}
        >
          Create Job Description
        </button>

        <button
          type="button"
          style={styles.secondaryButton}
          onClick={() => {
            console.log("Search Candidates clicked");
          }}
        >
          Search Candidates
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
    gap: "10px",
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
    backgroundColor: "#7c3aed",
    color: "#ffffff",
    fontSize: "15px",
    fontWeight: 600,
    cursor: "pointer",
  },

  errorMessage: {
    marginBottom: "20px",
    padding: "12px",
    borderRadius: "8px",
    backgroundColor: "#fee2e2",
    color: "#b91c1c",
  },
};