import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { uploadResume } from "../../api/studentApi";
import SkillList from "../../components/SkillList";

export default function ResumeUpload({
  embedded = false,
  onComplete,
}) {
  const navigate = useNavigate();

  const [file, setFile] = useState(null);
  const [result, setResult] = useState(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  function handleFileChange(event) {
    const selectedFile =
      event.target.files?.[0] || null;

    setFile(null);
    setResult(null);
    setError("");

    if (!selectedFile) {
      return;
    }

    const allowedTypes = [
      "application/pdf",
      "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
      "application/msword",
    ];

    const allowedExtensions = [
      ".pdf",
      ".docx",
      ".doc",
    ];

    const lowerFileName =
      selectedFile.name.toLowerCase();

    const hasAllowedExtension =
      allowedExtensions.some((extension) =>
        lowerFileName.endsWith(extension)
      );

    const hasAllowedType =
      allowedTypes.includes(selectedFile.type);

    if (!hasAllowedType && !hasAllowedExtension) {
      setError(
        "Only PDF, DOC, and DOCX files are allowed."
      );
      return;
    }

    const maxFileSize = 5 * 1024 * 1024;

    if (selectedFile.size > maxFileSize) {
      setError(
        "The file must not exceed 5 MB."
      );
      return;
    }

    setFile(selectedFile);
  }

  async function handleSubmit(event) {
    event.preventDefault();

    setError("");
    setResult(null);

    if (!file) {
      setError(
        "Please select a PDF, DOC, or DOCX resume."
      );
      return;
    }

    setLoading(true);

    try {
      const response = await uploadResume(file);

      setResult(response);

      if (onComplete) {
        onComplete(response);
      }
    } catch (requestError) {
      setError(
        requestError.response?.data?.message ||
        "Unable to upload resume."
      );
    } finally {
      setLoading(false);
    }
  }

  return (
    <main style={styles.page}>
      <section style={styles.card}>
        {!embedded && (
          <button
            type="button"
            style={styles.backButton}
            onClick={() => navigate("/student")}
          >
            ← Back to dashboard
          </button>
        )}

        <h1>Upload your resume</h1>

        <p style={styles.description}>
          Upload your current resume and Thrivya will
          extract readable text and identify your skills.
        </p>

        <p style={styles.supportedFormats}>
          Supported formats: PDF, DOC, and DOCX.
          Maximum size: 5 MB.
        </p>

        {error && (
          <p style={styles.error}>{error}</p>
        )}

        <form onSubmit={handleSubmit}>
          <label
            htmlFor="resumeFile"
            style={styles.label}
          >
            Resume file
          </label>

          <input
            id="resumeFile"
            type="file"
            accept="
              .pdf,
              .doc,
              .docx,
              application/pdf,
              application/msword,
              application/vnd.openxmlformats-officedocument.wordprocessingml.document
            "
            onChange={handleFileChange}
            style={styles.fileInput}
          />

          {file && (
            <div style={styles.fileInfo}>
              <p>
                <strong>Selected file:</strong>{" "}
                {file.name}
              </p>

              <p>
                <strong>Size:</strong>{" "}
                {(file.size / 1024).toFixed(1)} KB
              </p>

              <p>
                <strong>Type:</strong>{" "}
                {file.type || "Detected from extension"}
              </p>
            </div>
          )}

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
              ? "Uploading and analyzing..."
              : "Upload resume"}
          </button>
        </form>

        {result && (
          <section style={styles.result}>
            <p style={styles.success}>
              {result.message ||
                "Resume uploaded successfully."}
            </p>

            <h2>Extracted skills</h2>

            <SkillList
              skills={result.skills || []}
            />

            <h2>Extracted text preview</h2>

            {result.extractedText ? (
              <pre style={styles.preview}>
                {result.extractedText}
              </pre>
            ) : (
              <p style={styles.noText}>
                No readable text was found in this file.
              </p>
            )}

            {embedded && (
              <button
                type="button"
                onClick={() => {
                  if (onComplete) {
                    onComplete(result);
                  }
                }}
                style={styles.continueButton}
              >
                Continue to role selection
              </button>
            )}
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
    maxWidth: "900px",
    margin: "0 auto",
    padding: "32px",
    borderRadius: "18px",
    background: "white",
    boxShadow:
      "0 10px 30px rgba(15, 23, 42, 0.12)",
  },

  backButton: {
    border: "none",
    background: "transparent",
    color: "#2563eb",
    cursor: "pointer",
    marginBottom: "20px",
    padding: 0,
    fontSize: "15px",
  },

  description: {
    color: "#475569",
    lineHeight: 1.6,
  },

  supportedFormats: {
    color: "#64748b",
    fontSize: "14px",
    marginBottom: "24px",
  },

  label: {
    display: "block",
    marginBottom: "8px",
    fontWeight: 600,
    color: "#172033",
  },

  fileInput: {
    display: "block",
    width: "100%",
    marginBottom: "18px",
    padding: "12px",
    border: "1px solid #cbd5e1",
    borderRadius: "8px",
    background: "#ffffff",
  },

  fileInfo: {
    marginBottom: "18px",
    padding: "12px 16px",
    borderRadius: "8px",
    background: "#f8fafc",
    color: "#475569",
  },

  primaryButton: {
    display: "block",
    width: "100%",
    marginTop: "18px",
    padding: "13px 18px",
    border: "none",
    borderRadius: "8px",
    background: "#2563eb",
    color: "white",
    fontWeight: 600,
    cursor: "pointer",
  },

  continueButton: {
    display: "block",
    width: "100%",
    marginTop: "18px",
    padding: "13px 18px",
    border: "none",
    borderRadius: "8px",
    background: "#16a34a",
    color: "white",
    fontWeight: 600,
    cursor: "pointer",
  },

  disabledButton: {
    background: "#93c5fd",
    cursor: "not-allowed",
  },

  result: {
    marginTop: "32px",
    paddingTop: "24px",
    borderTop: "1px solid #e2e8f0",
  },

  preview: {
    maxHeight: "300px",
    overflowY: "auto",
    padding: "16px",
    borderRadius: "8px",
    background: "#f8fafc",
    color: "#334155",
    whiteSpace: "pre-wrap",
    wordBreak: "break-word",
    lineHeight: 1.5,
  },

  noText: {
    padding: "12px",
    borderRadius: "8px",
    background: "#fff7ed",
    color: "#c2410c",
  },

  error: {
    marginBottom: "18px",
    padding: "10px",
    borderRadius: "8px",
    background: "#fee2e2",
    color: "#b91c1c",
  },

  success: {
    marginBottom: "18px",
    padding: "10px",
    borderRadius: "8px",
    background: "#dcfce7",
    color: "#15803d",
  },
};