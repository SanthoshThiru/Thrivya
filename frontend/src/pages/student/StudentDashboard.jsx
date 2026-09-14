import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import DashboardCard from "../../components/DashboardCard";
import { getStudentDashboard } from "../../api/dashboardApi";

export default function StudentDashboard() {
  const navigate = useNavigate();

  const [dashboard, setDashboard] = useState(null);
  const [error, setError] = useState("");

  const storedUser =
    localStorage.getItem("thrivyaUser");

  let user = null;

  try {
    user = storedUser
      ? JSON.parse(storedUser)
      : null;
  } catch {
    user = null;
  }

  useEffect(() => {
    async function loadDashboard() {
      try {
        const data =
          await getStudentDashboard();

        setDashboard(data);
      } catch (requestError) {
        if (
          requestError.response?.status === 401
        ) {
          localStorage.removeItem(
            "thrivyaToken"
          );

          localStorage.removeItem(
            "thrivyaUser"
          );

          navigate("/login", {
            replace: true,
          });

          return;
        }

        setError(
          requestError.response?.data?.message ||
            "Unable to load student dashboard"
        );
      }
    }

    loadDashboard();
  }, [navigate]);

  const studentName =
    user?.username ||
    user?.userName ||
    user?.name ||
    "Student";

  function handleProfileSetup() {
    navigate("/student/setup");
  }

  function handleShowSkills() {
    navigate("/student/skills");
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
              <p style={styles.brand}>
                Thrivya
              </p>

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

          <p style={styles.error}>
            {error}
          </p>

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
        <section style={styles.card}>
          <p style={styles.brand}>
            Thrivya
          </p>

          <h1>Student Dashboard</h1>

          <p>
            Loading student dashboard...
          </p>
        </section>
      </main>
    );
  }

  return (
    <DashboardCard
      title={
        dashboard.title ||
        "Student Dashboard"
      }
      role={
        dashboard.role ||
        "STUDENT"
      }
      email={
        dashboard.email ||
        user?.email ||
        "Student"
      }
      message={
        dashboard.message ||
        "Your Thrivya career journey starts here."
      }
    >
      <p style={styles.welcomeText}>
        Welcome{" "}
        <strong>{studentName}</strong>
      </p>

      <div style={styles.infoBox}>
        <h2>
          Build your career profile
        </h2>

        <p>
          Upload your resume or enter your
          skills manually. Then choose a
          desired career role or receive
          relevant role suggestions.
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
      </div>
    </DashboardCard>
  );
}

const styles = {
  page: {
    minHeight: "100vh",
    padding: "32px 20px",
    background: "#f4f7fb",
  },

  card: {
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

  error: {
    padding: "12px",
    borderRadius: "8px",
    background: "#fee2e2",
    color: "#b91c1c",
  },
};