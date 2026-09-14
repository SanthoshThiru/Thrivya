import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { saveManualSkills } from "../../api/studentApi";
import SkillList from "../../components/SkillList";

export default function ManualSkills({
  embedded = false,
  onComplete,
}) {
  const navigate = useNavigate();

  const [skillsText, setSkillsText] =
    useState("");

  const [skills, setSkills] =
    useState([]);

  const [message, setMessage] =
    useState("");

  const [error, setError] =
    useState("");

  const [loading, setLoading] =
    useState(false);

  async function handleSubmit(event) {
    event.preventDefault();

    setMessage("");
    setError("");

    const cleanedSkillsText =
      skillsText.trim();

    if (!cleanedSkillsText) {
      setError(
        "Please enter at least one skill."
      );
      return;
    }

    setLoading(true);

    try {
      const result =
        await saveManualSkills(
          cleanedSkillsText
        );

      setSkills(result.skills || []);

      setMessage(
        result.message ||
          "Skills saved successfully."
      );

      if (onComplete) {
        onComplete();
      }
    } catch (requestError) {
      setError(
        requestError.response?.data?.message ||
          "Unable to save manual skills."
      );
    } finally {
      setLoading(false);
    }
  }

  return (
    <main
      style={
        embedded
          ? styles.embeddedPage
          : styles.page
      }
    >
      <section
        style={
          embedded
            ? styles.embeddedCard
            : styles.card
        }
      >
        {!embedded && (
          <button
            type="button"
            style={styles.backButton}
            onClick={() =>
              navigate("/student")
            }
          >
            ← Back to dashboard
          </button>
        )}

        <h1>Enter your skills</h1>

        <p style={styles.description}>
          Enter your skills separated by commas,
          write them in a sentence, or enter one
          skill per line.
        </p>

        <p style={styles.example}>
          Example: Java, SQL, Spring Boot, React,
          Docker, communication, teamwork
        </p>

        {error && (
          <p style={styles.error}>
            {error}
          </p>
        )}

        {message && (
          <p style={styles.success}>
            {message}
          </p>
        )}

        <form onSubmit={handleSubmit}>
          <label
            htmlFor="skillsText"
            style={styles.label}
          >
            Your current skills
          </label>

          <textarea
            id="skillsText"
            value={skillsText}
            onChange={(event) =>
              setSkillsText(event.target.value)
            }
            placeholder={
              "Example: Java, SQL, Spring Boot, React, Docker"
            }
            rows={8}
            maxLength={3000}
            style={styles.textarea}
            required
          />

          <p style={styles.counter}>
            {skillsText.length}/3000
          </p>

          <button
            type="submit"
            disabled={loading}
            style={{
              ...styles.primaryButton,
              ...(loading
                ? styles.disabledButton
                : {}),
            }}
          >
            {loading
              ? "Saving..."
              : "Save my skills"}
          </button>
        </form>

        {skills.length > 0 && (
          <section style={styles.result}>
            <h2>Detected skills</h2>

            <SkillList skills={skills} />
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

  embeddedPage: {
    width: "100%",
    padding: "0",
    background: "transparent",
  },

  card: {
    width: "100%",
    maxWidth: "760px",
    margin: "0 auto",
    padding: "32px",
    borderRadius: "18px",
    background: "white",
    boxShadow:
      "0 10px 30px rgba(15, 23, 42, 0.12)",
  },

  embeddedCard: {
    width: "100%",
    padding: "0",
    background: "transparent",
  },

  backButton: {
    marginBottom: "20px",
    border: "none",
    background: "transparent",
    color: "#2563eb",
    cursor: "pointer",
    fontSize: "15px",
  },

  description: {
    color: "#64748b",
    lineHeight: 1.6,
  },

  example: {
    padding: "12px",
    borderRadius: "8px",
    background: "#eff6ff",
    color: "#1d4ed8",
    lineHeight: 1.5,
  },

  label: {
    display: "block",
    marginTop: "22px",
    marginBottom: "8px",
    color: "#172033",
    fontWeight: 600,
  },

  textarea: {
    width: "100%",
    padding: "14px",
    border: "1px solid #cbd5e1",
    borderRadius: "10px",
    resize: "vertical",
    fontFamily: "inherit",
    fontSize: "15px",
    lineHeight: 1.5,
  },

  counter: {
    margin: "6px 0 0",
    color: "#64748b",
    fontSize: "13px",
    textAlign: "right",
  },

  primaryButton: {
    width: "100%",
    marginTop: "14px",
    padding: "13px",
    border: "none",
    borderRadius: "8px",
    background: "#2563eb",
    color: "white",
    fontSize: "16px",
    fontWeight: 600,
    cursor: "pointer",
  },

  disabledButton: {
    background: "#93c5fd",
    cursor: "not-allowed",
  },

  error: {
    padding: "10px",
    borderRadius: "8px",
    background: "#fee2e2",
    color: "#b91c1c",
  },

  success: {
    padding: "10px",
    borderRadius: "8px",
    background: "#dcfce7",
    color: "#15803d",
  },

  result: {
    marginTop: "28px",
    paddingTop: "20px",
    borderTop: "1px solid #e2e8f0",
  },
};