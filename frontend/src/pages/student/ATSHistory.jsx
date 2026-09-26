import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getATSHistory } from "../../api/studentApi";

export default function ATSHistory() {
  const navigate = useNavigate();

  const [history, setHistory] =
    useState([]);

  const [error, setError] =
    useState("");

  useEffect(() => {
    async function loadHistory() {
      try {
        const result =
          await getATSHistory();

        setHistory(result);
      } catch (requestError) {
        setError(
          requestError.response?.data?.message ||
          "Unable to load ATS history"
        );
      }
    }

    loadHistory();
  }, []);

  return (
    <main style={styles.page}>
      <section style={styles.card}>
        <button
          onClick={() => navigate("/student")}
        >
          Back to dashboard
        </button>

        <h1>ATS improvement history</h1>

        {error && (
          <p style={styles.error}>{error}</p>
        )}

        {history.length === 0 && !error && (
          <p>No ATS reports are available yet.</p>
        )}

        {history.map((item, index) => (
          <article
            key={item.reportId}
            style={styles.row}
          >
            <div>
              <h2>
                Resume Version {item.resumeVersion}
              </h2>

              <p>Role: {item.roleTitle}</p>

              <p>
                Generated:{" "}
                {new Date(
                  item.generatedAt
                ).toLocaleString()}
              </p>
            </div>

            <div style={styles.score}>
              {Math.round(item.overallScore)}/100
            </div>
          </article>
        ))}
      </section>
    </main>
  );
}

const styles = {
  page: {
    minHeight: "100vh",
    padding: "32px",
    background: "#f4f7fb",
  },
  card: {
    maxWidth: "850px",
    margin: "0 auto",
    padding: "32px",
    background: "white",
    borderRadius: "18px",
  },
  row: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    gap: "20px",
    padding: "18px 0",
    borderBottom: "1px solid #e2e8f0",
  },
  score: {
    color: "#2563eb",
    fontSize: "28px",
    fontWeight: 700,
  },
  error: {
    padding: "10px",
    background: "#fee2e2",
    color: "#991b1b",
  },
};