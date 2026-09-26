import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  generateATSReport,
  getLatestATSReport,
} from "../../api/studentApi";

export default function ATSReport() {
  const navigate = useNavigate();

  const [report, setReport] =
    useState(null);

  const [error, setError] =
    useState("");

  const [loading, setLoading] =
    useState(false);

  async function loadLatestReport() {
    try {
      const result =
        await getLatestATSReport();

      setReport(result);
    } catch {
      // No report may exist yet.
    }
  }

  useEffect(() => {
    loadLatestReport();
  }, []);

  async function handleGenerate() {
    setError("");
    setLoading(true);

    try {
      const result =
        await generateATSReport();

      setReport(result);
    } catch (requestError) {
      setError(
        requestError.response?.data?.message ||
        "Unable to generate ATS report"
      );
    } finally {
      setLoading(false);
    }
  }

  return (
    <main style={styles.page}>
      <section style={styles.card}>
        <button
          onClick={() => navigate("/student")}
        >
          Back to dashboard
        </button>

        <h1>Explainable ATS Report</h1>

        <p>
          This analysis uses your latest uploaded resume and
          selected career role.
        </p>

        {error && (
          <p style={styles.error}>{error}</p>
        )}

        <button
          onClick={handleGenerate}
          disabled={loading}
          style={styles.primaryButton}
        >
          {loading
            ? "Analyzing resume..."
            : "Generate ATS report"}
        </button>

        {report && (
          <section style={styles.report}>
            <div style={styles.scoreBox}>
              <p>Selected role</p>
              <h2>{report.roleTitle}</h2>
              <p style={styles.score}>
                {Math.round(report.overallScore)}
                <span>/100</span>
              </p>
              <p>Overall ATS score</p>
            </div>

            <p>{report.summary}</p>

            <h2>Score breakdown</h2>

            <div style={styles.factorGrid}>
              {report.factors?.map((factor) => (
                <article
                  key={factor.factorName}
                  style={styles.factorCard}
                >
                  <h3>{factor.factorName}</h3>

                  <p style={styles.factorScore}>
                    {Math.round(factor.score)}/100
                  </p>

                  <p>
                    Weight:{" "}
                    {Math.round(
                      factor.weight * 100
                    )}
                    %
                  </p>

                  {factor.evidence?.map(
                    (item, index) => (
                      <div
                        key={`${item.itemName}-${index}`}
                        style={styles.evidence}
                      >
                        <strong>
                          {item.itemName}
                        </strong>

                        <p>
                          Status: {item.status}
                        </p>

                        {item.evidenceText && (
                          <p>
                            Evidence:{" "}
                            {item.evidenceText}
                          </p>
                        )}

                        {item.recommendation && (
                          <p>
                            Action:{" "}
                            {item.recommendation}
                          </p>
                        )}
                      </div>
                    )
                  )}
                </article>
              ))}
            </div>
          </section>
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
    maxWidth: "1100px",
    margin: "0 auto",
    padding: "32px",
    background: "white",
    borderRadius: "18px",
  },
  primaryButton: {
    marginTop: "12px",
    padding: "12px 18px",
    border: "none",
    borderRadius: "8px",
    background: "#2563eb",
    color: "white",
    cursor: "pointer",
  },
  report: {
    marginTop: "30px",
  },
  scoreBox: {
    padding: "24px",
    borderRadius: "14px",
    background: "#eff6ff",
    textAlign: "center",
  },
  score: {
    margin: "8px 0",
    color: "#1d4ed8",
    fontSize: "56px",
    fontWeight: 700,
  },
  factorGrid: {
    display: "grid",
    gridTemplateColumns:
      "repeat(auto-fit, minmax(280px, 1fr))",
    gap: "18px",
  },
  factorCard: {
    padding: "18px",
    border: "1px solid #dbe3ef",
    borderRadius: "12px",
  },
  factorScore: {
    color: "#2563eb",
    fontSize: "28px",
    fontWeight: 700,
  },
  evidence: {
    marginTop: "12px",
    padding: "10px",
    background: "#f8fafc",
    borderRadius: "8px",
    fontSize: "13px",
  },
  error: {
    padding: "10px",
    color: "#b91c1c",
    background: "#fee2e2",
  },
};