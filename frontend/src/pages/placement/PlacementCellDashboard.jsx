import { useEffect, useState } from "react";

import DashboardCard from "../../components/DashboardCard";
import { getPlacementDashboard } from "../../api/dashboardApi";

export default function PlacementCellDashboard() {
  const [dashboard, setDashboard] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    async function loadDashboard() {
      try {
        const data = await getPlacementDashboard();
        setDashboard(data);
      } catch (requestError) {
        setError(
          requestError.response?.data?.message ||
            "Unable to load placement dashboard"
        );
      }
    }

    loadDashboard();
  }, []);

  if (error) {
    return (
      <DashboardCard
        title="Placement Cell Dashboard"
        role="PLACEMENT_CELL"
        email="Unknown user"
        message="Unable to load placement cell dashboard"
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
        <p>Loading placement dashboard...</p>
      </main>
    );
  }

  return (
    <DashboardCard
      title={dashboard.title || "Placement Cell Dashboard"}
      role={dashboard.role || "PLACEMENT_CELL"}
      email={dashboard.email || "Placement Officer"}
      message={
        dashboard.message ||
        "Placement cell dashboard accessed"
      }
    >
      <h2>College placement overview</h2>

      <p>
        Student groups, skill-gap summaries, and training
        planning will appear here.
      </p>

      <div style={styles.actions}>
        <button
          type="button"
          style={styles.primaryButton}
          onClick={() => {
            console.log("View Student Groups clicked");
          }}
        >
          View Student Groups
        </button>

        <button
          type="button"
          style={styles.secondaryButton}
          onClick={() => {
            console.log("View Skill Gaps clicked");
          }}
        >
          View Skill Gaps
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
    backgroundColor: "#0f766e",
    color: "#ffffff",
    fontSize: "15px",
    fontWeight: 600,
    cursor: "pointer",
  },

  error: {
    padding: "12px",
    borderRadius: "8px",
    backgroundColor: "#fee2e2",
    color: "#b91c1c",
  },
};