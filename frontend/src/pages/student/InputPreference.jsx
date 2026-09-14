import { useNavigate } from "react-router-dom";

export default function InputPreference() {
  const navigate = useNavigate();

  function openResumeUpload() {
    navigate("/student/resume-upload");
  }

  function openManualSkills() {
    navigate("/student/manual-skills");
  }

  return (
    <main style={styles.page}>
      <section style={styles.card}>
        <p style={styles.brand}>
          Thrivya
        </p>

        <h1>
          Build your career profile
        </h1>

        <p style={styles.description}>
          Choose how you want to provide your
          current skills and experience.
        </p>

        <div style={styles.options}>
          <button
            type="button"
            style={styles.option}
            onClick={openResumeUpload}
          >
            <h2>
              Upload resume
            </h2>

            <p>
              Thrivya will extract readable text
              and identify skills from your PDF
              or DOCX resume.
            </p>
          </button>

          <button
            type="button"
            style={styles.option}
            onClick={openManualSkills}
          >
            <h2>
              Enter skills manually
            </h2>

            <p>
              Enter or describe your current
              skills in your own words.
            </p>
          </button>
        </div>
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
    maxWidth: "800px",
    margin: "0 auto",
    padding: "32px",
    borderRadius: "18px",
    background: "#ffffff",
    boxShadow:
      "0 10px 30px rgba(15, 23, 42, 0.12)",
  },

  brand: {
    margin: "0 0 8px",
    color: "#2563eb",
    fontWeight: 700,
    fontSize: "18px",
  },

  description: {
    color: "#64748b",
    lineHeight: 1.6,
  },

  options: {
    display: "grid",
    gridTemplateColumns:
      "repeat(auto-fit, minmax(250px, 1fr))",
    gap: "20px",
    marginTop: "28px",
  },

  option: {
    padding: "24px",
    border: "1px solid #bfdbfe",
    borderRadius: "14px",
    background: "#eff6ff",
    color: "#172033",
    textAlign: "left",
    cursor: "pointer",
    lineHeight: 1.5,
  },
};