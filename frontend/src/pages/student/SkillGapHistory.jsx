import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getSkillGapHistory } from "../../api/studentApi";

export default function SkillGapHistory() {
  const navigate = useNavigate();

  const [data, setData] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    async function loadHistory() {
      try {
        const result = await getSkillGapHistory();

        setData(result);
      } catch (requestError) {
        setError(
          requestError.response?.data?.message ||
            "Unable to load skill-gap history"
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
          style={styles.backButton}
        >
          Back to dashboard
        </button>

        <h1>Skill-gap history</h1>

        {error && (
          <p style={styles.error}>{error}</p>
        )}

        {!data && !error && (
          <p>Loading skill-gap history...</p>
        )}

        {data && (
          <>
            <p>
              Role:{" "}
              <strong>{data.roleTitle}</strong>
            </p>

            {data.history?.length === 0 && (
              <p style={styles.muted}>
                No skill-gap history is available yet.
              </p>
            )}

            {data.history?.map((item, index) => (
              <article
                key={
                  item.analysisVersion ||
                  item.calculatedAt ||
                  index
                }
                style={styles.item}
              >
                <div>
                  <h2>
                    Analysis{" "}
                    {item.analysisVersion ??
                      index + 1}
                  </h2>

                  <p>
                    Resume version:{" "}
                    {item.resumeVersion ??
                      "Manual update"}
                  </p>

                  {item.calculatedAt && (
                    <p>
                      Calculated:{" "}
                      {new Date(
                        item.calculatedAt
                      ).toLocaleString()}
                    </p>
                  )}
                </div>

                <div style={styles.metrics}>
                  <span>
                    Readiness:{" "}
                    <strong>
                      {Math.round(
                        Number(
                          item.readinessPercentage || 0
                        )
                      )}
                      %
                    </strong>
                  </span>

                  <span>
                    Average gap:{" "}
                    <strong>
                      {Math.round(
                        Number(
                          item.averageGapPercentage || 0
                        )
                      )}
                      %
                    </strong>
                  </span>
                </div>
              </article>
            ))}
          </>
        )}
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
    maxWidth: "900px",
    margin: "0 auto",
    padding: "32px",
    background: "white",
    borderRadius: "18px",
    boxShadow:
      "0 5px 16px rgba(15, 23, 42, 0.06)",
  },

  backButton: {
    padding: "9px 14px",
    border: "1px solid #cbd5e1",
    borderRadius: "8px",
    background: "white",
    color: "#334155",
    cursor: "pointer",
  },

  item: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    gap: "20px",
    padding: "18px 0",
    borderBottom: "1px solid #e2e8f0",
  },

  metrics: {
    display: "grid",
    gap: "8px",
    textAlign: "right",
    color: "#334155",
  },

  muted: {
    color: "#64748b",
  },

  error: {
    padding: "10px",
    borderRadius: "8px",
    background: "#fee2e2",
    color: "#991b1b",
  },
};