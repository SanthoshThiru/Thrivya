import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { updateSkillProgress } from "../../api/studentApi";

export default function ProgressUpdate() {
  const navigate = useNavigate();

  const [form, setForm] =
    useState({
      skillName: "",
      updateText: "",
      reportedLevel: "0.5",
    });

  const [message, setMessage] =
    useState("");

  const [error, setError] =
    useState("");

  function handleChange(event) {
    const { name, value } =
      event.target;

    setForm((previous) => ({
      ...previous,
      [name]: value,
    }));
  }

  async function handleSubmit(event) {
    event.preventDefault();

    setMessage("");
    setError("");

    try {
      const result =
        await updateSkillProgress({
          skillName: form.skillName,
          updateText: form.updateText,
          reportedLevel:
            Number(form.reportedLevel),
        });

      setMessage(result.message);
    } catch (requestError) {
      setError(
        requestError.response?.data?.message ||
        "Unable to update skill progress"
      );
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

        <h1>Update learning progress</h1>

        <p>
          Describe what you have learned or practiced.
          This will update your current evidence and skill gap.
        </p>

        {message && (
          <p style={styles.success}>{message}</p>
        )}

        {error && (
          <p style={styles.error}>{error}</p>
        )}

        <form onSubmit={handleSubmit}>
          <label>Skill name</label>

          <input
            name="skillName"
            value={form.skillName}
            onChange={handleChange}
            placeholder="Example: Docker"
            required
            style={styles.input}
          />

          <label>Learning update</label>

          <textarea
            name="updateText"
            value={form.updateText}
            onChange={handleChange}
            placeholder="Example: Containerized my Spring Boot API and ran it locally."
            rows={6}
            required
            style={styles.input}
          />

          <label>
            Self-reported level:{" "}
            {Math.round(
              Number(form.reportedLevel) * 100
            )}
            %
          </label>

          <input
            type="range"
            name="reportedLevel"
            min="0"
            max="1"
            step="0.1"
            value={form.reportedLevel}
            onChange={handleChange}
          />

          <button
            type="submit"
            style={styles.primaryButton}
          >
            Save progress
          </button>
        </form>
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
    maxWidth: "700px",
    margin: "0 auto",
    padding: "32px",
    background: "white",
    borderRadius: "18px",
  },
  input: {
    display: "block",
    width: "100%",
    margin: "8px 0 18px",
    padding: "12px",
    border: "1px solid #cbd5e1",
    borderRadius: "8px",
  },
  primaryButton: {
    marginTop: "20px",
    padding: "12px 18px",
    border: "none",
    borderRadius: "8px",
    background: "#2563eb",
    color: "white",
  },
  success: {
    padding: "10px",
    background: "#dcfce7",
    color: "#166534",
  },
  error: {
    padding: "10px",
    background: "#fee2e2",
    color: "#991b1b",
  },
};