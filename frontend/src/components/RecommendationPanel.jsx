import { useState } from "react";
import { getRecommendations } from "../api/studentApi";

export default function RecommendationPanel({
  skillName,
}) {
  const [type, setType] =
    useState(null);

  const [resources, setResources] =
    useState([]);

  const [loading, setLoading] =
    useState(false);

  const [error, setError] =
    useState("");

  async function loadRecommendations(
    selectedType
  ) {
    setType(selectedType);
    setLoading(true);
    setError("");

    try {
      const result =
        await getRecommendations(
          skillName,
          selectedType
        );

      setResources(result);
    } catch (requestError) {
      setError(
        requestError.response?.data?.message ||
        "Unable to load recommendations"
      );
    } finally {
      setLoading(false);
    }
  }

  return (
    <div style={styles.panel}>
      <h3>Learn {skillName}</h3>

      <p style={styles.description}>
        Choose your preferred learning medium.
      </p>

      <div style={styles.buttons}>
        <button
          onClick={() =>
            loadRecommendations("DOCUMENT")
          }
          style={buttonStyle(
            type === "DOCUMENT"
          )}
        >
          Documents
        </button>

        <button
          onClick={() =>
            loadRecommendations("VIDEO")
          }
          style={buttonStyle(
            type === "VIDEO"
          )}
        >
          Videos
        </button>

        <button
          onClick={() =>
            loadRecommendations("AUDIO")
          }
          style={buttonStyle(
            type === "AUDIO"
          )}
        >
          Audios
        </button>
      </div>

      {loading && (
        <p>Loading current resources...</p>
      )}

      {error && (
        <p style={styles.error}>{error}</p>
      )}

      {!loading && resources.length > 0 && (
        <div style={styles.resources}>
          {resources.map((resource, index) => (
            <article
              key={
                resource.id ||
                `${resource.title}-${index}`
              }
              style={styles.resource}
            >
              <div style={styles.resourceHeader}>
                <h4>{resource.title}</h4>

                {resource.verified && (
                  <span style={styles.verified}>
                    Verified
                  </span>
                )}
              </div>

              <p>
                {resource.description}
              </p>

              <p style={styles.metadata}>
                {resource.provider} ·{" "}
                {resource.difficulty}

                {resource.estimatedMinutes
                  ? ` · ${resource.estimatedMinutes} min`
                  : ""}
              </p>

              <a
                href={resource.url}
                target="_blank"
                rel="noreferrer"
                style={styles.link}
              >
                Open resource
              </a>

              {resource.source ===
                "LIVE_SEARCH" && (
                <p style={styles.live}>
                  Live search result
                </p>
              )}
            </article>
          ))}
        </div>
      )}
    </div>
  );
}

function buttonStyle(active) {
  return {
    ...styles.button,
    ...(active
      ? styles.activeButton
      : {}),
  };
}

const styles = {
  panel: {
    marginTop: "16px",
    padding: "18px",
    borderRadius: "12px",
    background: "#eff6ff",
    border: "1px solid #bfdbfe",
  },
  description: {
    color: "#475569",
  },
  buttons: {
    display: "flex",
    flexWrap: "wrap",
    gap: "10px",
  },
  button: {
    padding: "9px 14px",
    border: "1px solid #2563eb",
    borderRadius: "8px",
    background: "white",
    color: "#2563eb",
    cursor: "pointer",
  },
  activeButton: {
    background: "#2563eb",
    color: "white",
  },
  resources: {
    display: "grid",
    gap: "12px",
    marginTop: "16px",
  },
  resource: {
    padding: "14px",
    borderRadius: "10px",
    background: "white",
    border: "1px solid #dbe3ef",
  },
  resourceHeader: {
    display: "flex",
    justifyContent: "space-between",
    gap: "12px",
  },
  verified: {
    height: "fit-content",
    padding: "4px 8px",
    borderRadius: "12px",
    background: "#dcfce7",
    color: "#166534",
    fontSize: "12px",
    fontWeight: 700,
  },
  metadata: {
    color: "#64748b",
    fontSize: "13px",
  },
  link: {
    color: "#2563eb",
    fontWeight: 600,
  },
  live: {
    color: "#92400e",
    fontSize: "12px",
  },
  error: {
    padding: "8px",
    background: "#fee2e2",
    color: "#991b1b",
  },
};