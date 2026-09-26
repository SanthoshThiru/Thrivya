import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getResumeHistory } from "../../api/studentApi";

export default function ResumeVersions() {
  const navigate = useNavigate();

  const [history, setHistory] =
    useState(null);

  const [error, setError] =
    useState("");

  useEffect(() => {
    async function loadHistory() {
      try {
        const result =
          await getResumeHistory();

        setHistory(result);
      } catch (requestError) {
        setError(
          requestError.response?.data?.message ||
          "Unable to load resume history"
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

        <h1>Resume version history</h1>

        {error && (
          <p style={styles.error}>{error}</p>
        )}

        {!history && !error && (
          <p>Loading resume history...</p>
        )}

        {history && (
          <>
            <p>
              Active version:{" "}
              <strong>
                Version {history.activeVersionNumber}
              </strong>
            </p>

            <div>
              {history.versions?.map((version) => (
                <article
                  key={version.resumeId}
                  style={styles.version}
                >
                  <div>
                    <h2>
                      Version {version.versionNumber}
                    </h2>

                    <p>
                      {version.originalFileName}
                    </p>

                    <p>
                      Uploaded:{" "}
                      {new Date(
                        version.uploadedAt
                      ).toLocaleString()}
                    </p>
                  </div>

                  <span
                    style={
                      version.active
                        ? styles.active
                        : styles.inactive
                    }
                  >
                    {version.active
                      ? "ACTIVE"
                      : "ARCHIVED"}
                  </span>
                </article>
              ))}
            </div>
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
    maxWidth: "850px",
    margin: "0 auto",
    padding: "32px",
    background: "white",
    borderRadius: "18px",
  },
  version: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    gap: "20px",
    padding: "18px 0",
    borderBottom: "1px solid #e2e8f0",
  },
  active: {
    padding: "6px 10px",
    borderRadius: "12px",
    background: "#dcfce7",
    color: "#166534",
    fontWeight: 700,
    fontSize: "12px",
  },
  inactive: {
    padding: "6px 10px",
    borderRadius: "12px",
    background: "#e2e8f0",
    color: "#475569",
    fontWeight: 700,
    fontSize: "12px",
  },
  error: {
    padding: "10px",
    background: "#fee2e2",
    color: "#991b1b",
  },
};