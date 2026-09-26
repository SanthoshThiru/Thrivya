import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getSkillGapHistory } from "../../api/studentApi";

export default function SkillGapHistory() {
  const navigate = useNavigate();

  const [data, setData] =
    useState(null);

  const [error, setError] =
    useState("");

  useEffect(() => {
    async function loadHistory() {
      try {
        const result =
          await getSkillGapHistory();

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
        >
          Back to dashboard
        </button>

        <h1>Skill-gap history</h1>

        {error && (
          <p style={styles.error}>{error}</p>
        )}

        {data && (
          <>
            <p>
              Role:{" "}
              <strong>{data.roleTitle}</strong>
            </p>

            {data.history.map((item) => (
              <article
                key={item.analysisVersion}
                style={styles.item}
              >
                <div>
                  <h2>
                    Analysis {item.analysisVersion}
                  </h2>

                  <p>
                    Resume version:{" "}
                    {item.resumeVersion ?? "Manual update"}
                  </p>

                  <p>
                    Calculated:{" "}
                    {new Date(
                      item.calculatedAt
                    ).toLocaleString()}
                  </p>
                </div>

                <div style={styles.metrics}>
                  <span>
                    Readiness:{" "}
                    <strong>
                      {Math.round(
                        item.readinessPercentage
                      )}
                      %
                    </strong>
                  </span>

                  <span>
                    Average gap:{" "}
                    <strong>
                      {Math.round(
                        item.averageGapPercentage
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
  },
  item: {
    display: "flex",
    justifyContent: "space-between",
    gap: "20px",
    padding: "18px 0",
    borderBottom: "1px solid #e2e8f0",
  },
  metrics: {
    display: "grid",
    gap: "8px",
    textAlign: "right",
  },
  error: {
    padding: "10px",
    background: "#fee2e2",
    color: "#991b1b",
  },
};