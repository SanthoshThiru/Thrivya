import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { createProjectEvidence } from "../../api/studentApi";

export default function ProjectEvidence() {
  const navigate = useNavigate();

  const [form, setForm] = useState({
    projectName: "",
    projectDescription: "",
    studentAction: "",
    resultText: "",
    githubUrl: "",
    liveUrl: "",
    skillNames: "",
  });

  const [message, setMessage] =
    useState("");

  const [error, setError] =
    useState("");

  const [loading, setLoading] =
    useState(false);

  function handleChange(event) {
    const { name, value } = event.target;

    setForm((previous) => ({
      ...previous,
      [name]: value,
    }));
  }

  async function handleSubmit(event) {
    event.preventDefault();

    setMessage("");
    setError("");
    setLoading(true);

    try {
      const payload = {
        projectName: form.projectName,
        projectDescription:
          form.projectDescription,
        studentAction: form.studentAction,
        resultText: form.resultText,
        githubUrl: form.githubUrl,
        liveUrl: form.liveUrl,
        skillNames: form.skillNames
          .split(",")
          .map((skill) => skill.trim())
          .filter(Boolean),
      };

      await createProjectEvidence(payload);

      setMessage(
        "Project evidence saved successfully."
      );

      setForm({
        projectName: "",
        projectDescription: "",
        studentAction: "",
        resultText: "",
        githubUrl: "",
        liveUrl: "",
        skillNames: "",
      });
    } catch (requestError) {
      setError(
        requestError.response?.data?.message ||
        "Unable to save project evidence."
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

        <h1>Add project evidence</h1>

        <p style={styles.note}>
          Enter only real projects and your real contribution.
          Thrivya uses this information to generate evidence-aware
          resume content.
        </p>

        {message && (
          <p style={styles.success}>
            {message}
          </p>
        )}

        {error && (
          <p style={styles.error}>
            {error}
          </p>
        )}

        <form onSubmit={handleSubmit}>
          <label>Project name</label>

          <input
            name="projectName"
            value={form.projectName}
            onChange={handleChange}
            placeholder="Example: Thrivya"
            required
            style={styles.input}
          />

          <label>Project description</label>

          <textarea
            name="projectDescription"
            value={form.projectDescription}
            onChange={handleChange}
            placeholder="What is this project?"
            rows={4}
            required
            style={styles.input}
          />

          <label>Your contribution</label>

          <textarea
            name="studentAction"
            value={form.studentAction}
            onChange={handleChange}
            placeholder="Example: Built authentication and role-based dashboards using Spring Boot and React."
            rows={5}
            required
            style={styles.input}
          />

          <label>Result or outcome</label>

          <textarea
            name="resultText"
            value={form.resultText}
            onChange={handleChange}
            placeholder="Optional: Example: Completed a working end-to-end login and dashboard workflow."
            rows={3}
            style={styles.input}
          />

          <label>Skills used</label>

          <input
            name="skillNames"
            value={form.skillNames}
            onChange={handleChange}
            placeholder="Java, Spring Boot, React, PostgreSQL"
            style={styles.input}
          />

          <label>GitHub URL</label>

          <input
            name="githubUrl"
            value={form.githubUrl}
            onChange={handleChange}
            placeholder="Optional GitHub repository URL"
            style={styles.input}
          />

          <label>Live project URL</label>

          <input
            name="liveUrl"
            value={form.liveUrl}
            onChange={handleChange}
            placeholder="Optional deployed project URL"
            style={styles.input}
          />

          <button
            type="submit"
            disabled={loading}
            style={styles.primaryButton}
          >
            {loading
              ? "Saving..."
              : "Save project evidence"}
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
    maxWidth: "780px",
    margin: "0 auto",
    padding: "32px",
    background: "white",
    borderRadius: "18px",
  },
  note: {
    padding: "12px",
    borderRadius: "8px",
    background: "#eff6ff",
    color: "#1e3a8a",
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
    padding: "12px 18px",
    border: "none",
    borderRadius: "8px",
    background: "#2563eb",
    color: "white",
    cursor: "pointer",
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