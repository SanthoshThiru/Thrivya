import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { generateResumeContent } from "../../api/studentApi";

export default function ResumeContent() {
  const navigate = useNavigate();

  const [content, setContent] = useState(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [copiedSection, setCopiedSection] = useState("");

  async function handleGenerate() {
    setLoading(true);
    setError("");
    setContent(null);
    setCopiedSection("");

    try {
      const result = await generateResumeContent();
      setContent(result);
    } catch (requestError) {
      setError(
        requestError.response?.data?.message ||
          "Unable to generate resume content. Please ensure that you have an uploaded resume and a selected role."
      );
    } finally {
      setLoading(false);
    }
  }

  async function handleCopy(sectionName, text) {
    if (!text || !text.trim()) {
      setError("There is no content available to copy.");
      return;
    }

    try {
      await navigator.clipboard.writeText(text);

      setCopiedSection(sectionName);
      setError("");

      window.setTimeout(() => {
        setCopiedSection("");
      }, 2000);
    } catch {
      setError(
        "Copy failed. Please select the text manually and copy it."
      );
    }
  }

  return (
    <main style={styles.page}>
      <section style={styles.container}>
        <button
          type="button"
          onClick={() => navigate("/student")}
          style={styles.backButton}
        >
          ← Back to dashboard
        </button>

        <section style={styles.headerCard}>
          <p style={styles.brand}>Thrivya</p>

          <h1 style={styles.title}>
            Evidence-aware resume content
          </h1>

          <p style={styles.description}>
            Generate copy-ready resume content using your active
            resume, selected role, verified skills, and learning
            progress evidence.
          </p>

          <p style={styles.note}>
            Thrivya does not automatically add unsupported
            employment experience, certifications, job titles,
            achievements, years of experience, or fake metrics.
            Review all generated content before adding it to your
            resume.
          </p>

          <button
            type="button"
            onClick={handleGenerate}
            disabled={loading}
            style={{
              ...styles.generateButton,
              ...(loading ? styles.disabledButton : {}),
            }}
          >
            {loading
              ? "Generating resume content..."
              : "Generate resume content"}
          </button>
        </section>

        {error && (
          <p style={styles.errorMessage}>
            {error}
          </p>
        )}

        {loading && (
          <section style={styles.loadingCard}>
            <h2 style={styles.loadingTitle}>
              Analyzing your evidence...
            </h2>

            <p style={styles.loadingText}>
              Thrivya is checking your latest resume, selected
              role, skill profile, ATS results, and learning
              progress before creating safe resume content.
            </p>
          </section>
        )}

        {content && !loading && (
          <>
            <section style={styles.statusCard}>
              <StatusItem
                label="Selected role"
                value={
                  content.selectedRole ||
                  "No role selected"
                }
              />

              <StatusItem
                label="Resume version"
                value={
                  content.resumeVersion
                    ? `Version ${content.resumeVersion}`
                    : "Not available"
                }
              />

              <StatusItem
                label="Evidence status"
                value={formatStatus(
                  content.evidenceStatus
                )}
                valueStyle={getEvidenceStatusStyle(
                  content.evidenceStatus
                )}
              />
            </section>

            <ContentSection
              title="Professional summary"
              sectionName="summary"
              text={content.summary}
              copiedSection={copiedSection}
              onCopy={handleCopy}
              helperText="Use this as a starting point. Confirm that the selected career role and listed skills accurately represent your real profile."
            />

            <ContentSection
              title="Technical skills"
              sectionName="skills"
              text={content.skills}
              copiedSection={copiedSection}
              onCopy={handleCopy}
              helperText="Copy only the skills that you can confidently explain and demonstrate during an interview."
            />

            <ContentSection
              title="Project content"
              sectionName="projects"
              text={content.projects}
              copiedSection={copiedSection}
              onCopy={handleCopy}
              helperText="Edit this content with your actual project name, real contribution, technologies used, and only confirmed outcomes."
            />

            <ContentSection
              title="Experience content"
              sectionName="experience"
              text={content.experience}
              copiedSection={copiedSection}
              onCopy={handleCopy}
              helperText="Do not describe learning activities, online courses, or self-practice as employment experience unless you actually worked in that role."
            />

            <section style={styles.evidenceSection}>
              <div style={styles.evidenceHeading}>
                <p style={styles.sectionEyebrow}>
                  Transparency and safety
                </p>

                <h2 style={styles.sectionTitle}>
                  Evidence used for generation
                </h2>

                <p style={styles.sectionDescription}>
                  Review the evidence behind every skill before
                  copying generated content into your final resume.
                </p>
              </div>

              {!content.evidence ||
              content.evidence.length === 0 ? (
                <section style={styles.emptyEvidence}>
                  <p>
                    No evidence records were returned. Upload a
                    resume, add skills, or record learning progress
                    before generating resume content.
                  </p>
                </section>
              ) : (
                <div style={styles.evidenceGrid}>
                  {content.evidence.map((item, index) => (
                    <EvidenceCard
                      key={`${item.claim}-${index}`}
                      item={item}
                    />
                  ))}
                </div>
              )}
            </section>
          </>
        )}
      </section>
    </main>
  );
}

function StatusItem({
  label,
  value,
  valueStyle,
}) {
  return (
    <div style={styles.statusItem}>
      <span style={styles.statusLabel}>
        {label}
      </span>

      <strong style={valueStyle}>
        {value}
      </strong>
    </div>
  );
}

function ContentSection({
  title,
  sectionName,
  text,
  copiedSection,
  onCopy,
  helperText,
}) {
  const [editableText, setEditableText] = useState(
    text || ""
  );

  return (
    <section style={styles.contentSection}>
      <div style={styles.contentSectionHeader}>
        <div>
          <p style={styles.sectionEyebrow}>
            Resume draft section
          </p>

          <h2 style={styles.sectionTitle}>
            {title}
          </h2>
        </div>

        <button
          type="button"
          onClick={() =>
            onCopy(sectionName, editableText)
          }
          style={styles.copyButton}
        >
          {copiedSection === sectionName
            ? "Copied ✓"
            : "Copy"}
        </button>
      </div>

      <textarea
        value={editableText}
        onChange={(event) =>
          setEditableText(event.target.value)
        }
        rows={
          sectionName === "summary" ||
          sectionName === "skills"
            ? 5
            : 8
        }
        style={styles.textarea}
      />

      <p style={styles.helperText}>
        {helperText}
      </p>
    </section>
  );
}

function EvidenceCard({ item }) {
  const isVerified =
    item.status === "VERIFIED";

  const isUserConfirmed =
    item.status === "USER_CONFIRMED";

  const requiresReview =
    item.requiresUserConfirmation === true ||
    item.status === "REQUIRES_CONFIRMATION";

  const cardStyle =
    isVerified
      ? styles.verifiedCard
      : isUserConfirmed
      ? styles.confirmedCard
      : styles.reviewCard;

  const badgeStyle =
    isVerified
      ? styles.verifiedBadge
      : isUserConfirmed
      ? styles.confirmedBadge
      : styles.reviewBadge;

  return (
    <article
      style={{
        ...styles.evidenceCard,
        ...cardStyle,
      }}
    >
      <div style={styles.evidenceCardHeader}>
        <h3 style={styles.claimTitle}>
          {item.claim || "Unnamed skill"}
        </h3>

        <span style={badgeStyle}>
          {formatStatus(item.status)}
        </span>
      </div>

      <p style={styles.evidenceDetail}>
        <strong>Source:</strong>{" "}
        {formatStatus(item.source)}
      </p>

      {item.evidenceText && (
        <div style={styles.evidenceTextBox}>
          <strong>Evidence:</strong>

          <p style={styles.evidenceText}>
            {cleanEvidenceText(item.evidenceText)}
          </p>
        </div>
      )}

      {isVerified && (
        <p style={styles.verifiedMessage}>
          Verified evidence: this skill was found in the
          uploaded resume or documented project evidence.
        </p>
      )}

      {isUserConfirmed && (
        <p style={styles.confirmedMessage}>
          User-confirmed evidence: review this wording before
          adding it to the final resume.
        </p>
      )}

      {requiresReview && (
        <p style={styles.reviewWarning}>
          Review required: this claim needs stronger evidence
          or user confirmation before it should appear in a
          resume.
        </p>
      )}

      {!isVerified &&
        !isUserConfirmed &&
        !requiresReview && (
          <p style={styles.notAutomaticallyUsed}>
            This evidence is not automatically treated as a
            verified resume claim.
          </p>
        )}
    </article>
  );
}

function cleanEvidenceText(text) {
  if (!text) {
    return "No evidence text is available.";
  }

  const cleaned = text
    .replace(/\s+/g, " ")
    .trim();

  if (cleaned.length <= 220) {
    return cleaned;
  }

  return `${cleaned.slice(0, 220)}...`;
}

function formatStatus(value) {
  if (!value) {
    return "Unknown";
  }

  return value
    .replaceAll("_", " ")
    .toLowerCase()
    .replace(/\b\w/g, (character) =>
      character.toUpperCase()
    );
}

function getEvidenceStatusStyle(status) {
  if (status === "EVIDENCE_SUPPORTED") {
    return {
      color: "#166534",
    };
  }

  if (status === "PARTIALLY_SUPPORTED") {
    return {
      color: "#92400e",
    };
  }

  return {
    color: "#991b1b",
  };
}

const styles = {
  page: {
    minHeight: "100vh",
    padding: "32px",
    background: "#f4f7fb",
    color: "#172033",
  },

  container: {
    width: "100%",
    maxWidth: "1050px",
    margin: "0 auto",
  },

  backButton: {
    marginBottom: "18px",
    padding: "9px 14px",
    border: "1px solid #cbd5e1",
    borderRadius: "8px",
    background: "#ffffff",
    color: "#334155",
    cursor: "pointer",
    fontWeight: 600,
  },

  headerCard: {
    padding: "32px",
    borderRadius: "18px",
    background:
      "linear-gradient(135deg, #eff6ff 0%, #f8fafc 100%)",
    border: "1px solid #bfdbfe",
  },

  brand: {
    margin: 0,
    color: "#2563eb",
    fontSize: "17px",
    fontWeight: 800,
    letterSpacing: "0.4px",
  },

  title: {
    margin: "10px 0 12px",
    fontSize: "32px",
  },

  description: {
    maxWidth: "760px",
    margin: 0,
    color: "#475569",
    lineHeight: 1.6,
  },

  note: {
    maxWidth: "760px",
    marginTop: "18px",
    padding: "13px",
    borderRadius: "9px",
    background: "#fef3c7",
    color: "#78350f",
    lineHeight: 1.5,
  },

  generateButton: {
    marginTop: "22px",
    padding: "13px 20px",
    border: "none",
    borderRadius: "9px",
    background: "#2563eb",
    color: "#ffffff",
    fontSize: "15px",
    fontWeight: 700,
    cursor: "pointer",
  },

  disabledButton: {
    background: "#93c5fd",
    cursor: "not-allowed",
  },

  errorMessage: {
    marginTop: "18px",
    padding: "12px",
    borderRadius: "9px",
    background: "#fee2e2",
    color: "#991b1b",
  },

  loadingCard: {
    marginTop: "22px",
    padding: "24px",
    borderRadius: "14px",
    background: "#ffffff",
    border: "1px solid #dbeafe",
  },

  loadingTitle: {
    marginTop: 0,
  },

  loadingText: {
    marginBottom: 0,
    color: "#475569",
  },

  statusCard: {
    display: "grid",
    gridTemplateColumns:
      "repeat(auto-fit, minmax(180px, 1fr))",
    gap: "14px",
    marginTop: "24px",
    padding: "18px",
    borderRadius: "14px",
    background: "#ffffff",
    boxShadow: "0 4px 14px rgba(15, 23, 42, 0.06)",
  },

  statusItem: {
    display: "flex",
    flexDirection: "column",
    gap: "6px",
  },

  statusLabel: {
    color: "#64748b",
    fontSize: "13px",
    fontWeight: 600,
  },

  contentSection: {
    marginTop: "22px",
    padding: "24px",
    borderRadius: "14px",
    background: "#ffffff",
    boxShadow: "0 4px 14px rgba(15, 23, 42, 0.06)",
  },

  contentSectionHeader: {
    display: "flex",
    alignItems: "center",
    justifyContent: "space-between",
    gap: "18px",
    marginBottom: "14px",
  },

  sectionEyebrow: {
    margin: 0,
    color: "#2563eb",
    fontSize: "12px",
    fontWeight: 800,
    letterSpacing: "0.5px",
    textTransform: "uppercase",
  },

  sectionTitle: {
    margin: "5px 0 0",
    fontSize: "22px",
  },

  sectionDescription: {
    margin: "8px 0 0",
    color: "#64748b",
    lineHeight: 1.5,
  },

  copyButton: {
    padding: "9px 15px",
    border: "none",
    borderRadius: "8px",
    background: "#0f766e",
    color: "#ffffff",
    fontWeight: 700,
    cursor: "pointer",
  },

  textarea: {
    width: "100%",
    padding: "14px",
    border: "1px solid #cbd5e1",
    borderRadius: "10px",
    background: "#f8fafc",
    color: "#172033",
    fontSize: "15px",
    fontFamily: "Arial, sans-serif",
    lineHeight: 1.55,
    resize: "vertical",
  },

  helperText: {
    margin: "10px 0 0",
    color: "#64748b",
    fontSize: "13px",
    lineHeight: 1.45,
  },

  evidenceSection: {
    marginTop: "22px",
    padding: "24px",
    borderRadius: "14px",
    background: "#ffffff",
    boxShadow: "0 4px 14px rgba(15, 23, 42, 0.06)",
  },

  evidenceHeading: {
    marginBottom: "18px",
  },

  evidenceGrid: {
    display: "grid",
    gridTemplateColumns:
      "repeat(auto-fit, minmax(280px, 1fr))",
    gap: "14px",
  },

  evidenceCard: {
    padding: "16px",
    borderRadius: "11px",
    border: "1px solid #e2e8f0",
  },

  verifiedCard: {
    background: "#f0fdf4",
    borderColor: "#bbf7d0",
  },

  confirmedCard: {
    background: "#eff6ff",
    borderColor: "#bfdbfe",
  },

  reviewCard: {
    background: "#fffbeb",
    borderColor: "#fde68a",
  },

  evidenceCardHeader: {
    display: "flex",
    alignItems: "center",
    justifyContent: "space-between",
    gap: "12px",
  },

  claimTitle: {
    margin: 0,
    fontSize: "17px",
  },

  verifiedBadge: {
    padding: "5px 9px",
    borderRadius: "14px",
    background: "#dcfce7",
    color: "#166534",
    fontSize: "11px",
    fontWeight: 800,
    whiteSpace: "nowrap",
  },

  confirmedBadge: {
    padding: "5px 9px",
    borderRadius: "14px",
    background: "#dbeafe",
    color: "#1d4ed8",
    fontSize: "11px",
    fontWeight: 800,
    whiteSpace: "nowrap",
  },

  reviewBadge: {
    padding: "5px 9px",
    borderRadius: "14px",
    background: "#fef3c7",
    color: "#92400e",
    fontSize: "11px",
    fontWeight: 800,
    whiteSpace: "nowrap",
  },

  evidenceDetail: {
    margin: "12px 0 0",
    color: "#475569",
    fontSize: "13px",
  },

  evidenceTextBox: {
    marginTop: "12px",
    padding: "10px",
    borderRadius: "8px",
    background: "rgba(255, 255, 255, 0.72)",
    color: "#475569",
    fontSize: "13px",
  },

  evidenceText: {
    margin: "7px 0 0",
    lineHeight: 1.5,
  },

  reviewWarning: {
    marginTop: "12px",
    padding: "8px",
    borderRadius: "6px",
    background: "#fef3c7",
    color: "#92400e",
    fontSize: "13px",
    fontWeight: 600,
    lineHeight: 1.45,
  },

  notAutomaticallyUsed: {
    marginTop: "12px",
    padding: "8px",
    borderRadius: "6px",
    background: "#fee2e2",
    color: "#991b1b",
    fontSize: "13px",
    fontWeight: 600,
    lineHeight: 1.45,
  },

  verifiedMessage: {
    marginTop: "12px",
    padding: "8px",
    borderRadius: "6px",
    background: "#dcfce7",
    color: "#166534",
    fontSize: "13px",
    fontWeight: 600,
    lineHeight: 1.45,
  },

  confirmedMessage: {
    marginTop: "12px",
    padding: "8px",
    borderRadius: "6px",
    background: "#dbeafe",
    color: "#1d4ed8",
    fontSize: "13px",
    fontWeight: 600,
    lineHeight: 1.45,
  },

  emptyEvidence: {
    padding: "18px",
    borderRadius: "10px",
    background: "#f8fafc",
    color: "#475569",
  },
};