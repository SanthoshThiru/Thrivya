import { useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  getRoleSuggestions,
  searchDesiredRole,
  selectRole,
} from "../../api/studentApi";

export default function RoleSelection() {
  const navigate = useNavigate();

  const [desiredRole, setDesiredRole] =
    useState("");

  const [roles, setRoles] =
    useState([]);

  const [selectedRoleId, setSelectedRoleId] =
    useState("");

  const [message, setMessage] =
    useState("");

  const [error, setError] =
    useState("");

  const [loading, setLoading] =
    useState(false);

  const [roleSelected, setRoleSelected] =
    useState(false);

  async function handleSuggestions() {
    setError("");
    setMessage("");
    setRoleSelected(false);
    setSelectedRoleId("");
    setLoading(true);

    try {
      const result =
        await getRoleSuggestions();

      setRoles(result);
    } catch (requestError) {
      setError(
        requestError.response?.data?.message ||
          "Unable to generate role suggestions"
      );
    } finally {
      setLoading(false);
    }
  }

  async function handleDesiredRole(event) {
    event.preventDefault();

    setError("");
    setMessage("");
    setRoleSelected(false);
    setSelectedRoleId("");
    setLoading(true);

    try {
      const result =
        await searchDesiredRole(desiredRole);

      setRoles(result);
    } catch (requestError) {
      setError(
        requestError.response?.data?.message ||
          "Unable to search for the desired role"
      );
    } finally {
      setLoading(false);
    }
  }

  async function handleRoleSelection() {
    if (!selectedRoleId) {
      setError("Please choose a role first");
      return;
    }

    setError("");
    setMessage("");
    setRoleSelected(false);
    setLoading(true);

    try {
      const result =
        await selectRole(Number(selectedRoleId));

      setMessage(
        result.message ||
          "Career role selected successfully"
      );

      setRoleSelected(true);
    } catch (requestError) {
      setError(
        requestError.response?.data?.message ||
          "Unable to select role"
      );
    } finally {
      setLoading(false);
    }
  }

  function handleGenerateATS() {
    navigate("/student/ats");
  }

  return (
    <section style={styles.container}>
      <h2>Choose your target role</h2>

      <p style={styles.description}>
        Enter a desired role yourself or ask Thrivya to
        suggest up to five relevant roles based on your
        current skills.
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

      <section style={styles.section}>
        <h3>Enter a desired role</h3>

        <form onSubmit={handleDesiredRole}>
          <input
            type="text"
            value={desiredRole}
            onChange={(event) =>
              setDesiredRole(event.target.value)
            }
            placeholder="Example: Java Backend Developer"
            style={styles.input}
            required
          />

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
              ? "Searching..."
              : "Search this role"}
          </button>
        </form>
      </section>

      <section style={styles.section}>
        <h3>Get role suggestions</h3>

        <p style={styles.helperText}>
          Thrivya will analyze your current skills and
          return a maximum of five relevant roles.
        </p>

        <button
          type="button"
          onClick={handleSuggestions}
          disabled={loading}
          style={{
            ...styles.secondaryButton,
            ...(loading
              ? styles.secondaryDisabledButton
              : {}),
          }}
        >
          {loading
            ? "Analyzing..."
            : "Suggest roles for me"}
        </button>
      </section>

      {roles.length > 0 && (
        <section style={styles.section}>
          <h3>Available roles</h3>

          <p style={styles.helperText}>
            Select one role to continue.
          </p>

          <div>
            {roles.map((role) => {
              const isSelected =
                String(selectedRoleId) ===
                String(role.roleId);

              return (
                <label
                  key={role.roleId}
                  style={{
                    ...styles.roleCard,
                    ...(isSelected
                      ? styles.selectedRoleCard
                      : {}),
                  }}
                >
                  <input
                    type="radio"
                    name="selectedRole"
                    value={role.roleId}
                    checked={isSelected}
                    onChange={(event) => {
                      setSelectedRoleId(
                        event.target.value
                      );
                      setRoleSelected(false);
                      setMessage("");
                      setError("");
                    }}
                  />

                  <span style={styles.roleContent}>
                    <strong style={styles.roleTitle}>
                      {role.rankNumber}. {role.title}
                    </strong>

                    <span style={styles.relevance}>
                      Relevance:{" "}
                      {Math.round(
                        Number(role.relevanceScore) * 100
                      )}
                      %
                    </span>

                    <span style={styles.roleDescription}>
                      {role.description}
                    </span>
                  </span>
                </label>
              );
            })}
          </div>

          <button
            type="button"
            onClick={handleRoleSelection}
            disabled={loading || !selectedRoleId}
            style={{
              ...styles.primaryButton,
              ...(!selectedRoleId || loading
                ? styles.disabledButton
                : {}),
            }}
          >
            {loading
              ? "Saving..."
              : "Select this role"}
          </button>

          {roleSelected && (
            <div style={styles.atsBox}>
              <h3>Role selected successfully</h3>

              <p style={styles.helperText}>
                You can now generate an explainable ATS
                report using your selected role and latest
                uploaded resume.
              </p>

              <button
                type="button"
                onClick={handleGenerateATS}
                style={styles.atsButton}
              >
                Generate ATS report
              </button>
            </div>
          )}
        </section>
      )}
    </section>
  );
}

const styles = {
  container: {
    width: "100%",
  },

  description: {
    marginTop: "8px",
    color: "#64748b",
    lineHeight: 1.6,
  },

  section: {
    marginTop: "28px",
    paddingTop: "20px",
    borderTop: "1px solid #e2e8f0",
  },

  helperText: {
    color: "#64748b",
    fontSize: "14px",
    lineHeight: 1.5,
  },

  input: {
    width: "100%",
    padding: "12px",
    border: "1px solid #cbd5e1",
    borderRadius: "8px",
    fontSize: "15px",
    outline: "none",
  },

  primaryButton: {
    marginTop: "12px",
    padding: "12px 18px",
    border: "none",
    borderRadius: "8px",
    background: "#2563eb",
    color: "white",
    cursor: "pointer",
    fontSize: "14px",
    fontWeight: 600,
  },

  secondaryButton: {
    padding: "12px 18px",
    border: "1px solid #2563eb",
    borderRadius: "8px",
    background: "white",
    color: "#2563eb",
    cursor: "pointer",
    fontSize: "14px",
    fontWeight: 600,
  },

  disabledButton: {
    background: "#94a3b8",
    cursor: "not-allowed",
  },

  secondaryDisabledButton: {
    color: "#94a3b8",
    borderColor: "#94a3b8",
    cursor: "not-allowed",
  },

  roleCard: {
    display: "flex",
    alignItems: "flex-start",
    gap: "12px",
    marginBottom: "12px",
    padding: "16px",
    border: "1px solid #dbe3ef",
    borderRadius: "10px",
    background: "#ffffff",
    cursor: "pointer",
    transition: "all 0.2s ease",
  },

  selectedRoleCard: {
    border: "2px solid #2563eb",
    background: "#eff6ff",
  },

  roleContent: {
    display: "flex",
    flexDirection: "column",
    gap: "6px",
  },

  roleTitle: {
    color: "#172033",
    fontSize: "16px",
  },

  relevance: {
    color: "#2563eb",
    fontSize: "13px",
    fontWeight: 600,
  },

  roleDescription: {
    color: "#64748b",
    fontSize: "14px",
    lineHeight: 1.5,
  },

  atsBox: {
    marginTop: "24px",
    padding: "20px",
    border: "1px solid #86efac",
    borderRadius: "12px",
    background: "#f0fdf4",
  },

  atsButton: {
    marginTop: "10px",
    padding: "12px 18px",
    border: "none",
    borderRadius: "8px",
    background: "#16a34a",
    color: "white",
    cursor: "pointer",
    fontSize: "14px",
    fontWeight: 600,
  },

  error: {
    padding: "10px 12px",
    borderRadius: "8px",
    color: "#b91c1c",
    background: "#fee2e2",
  },

  success: {
    padding: "10px 12px",
    borderRadius: "8px",
    color: "#15803d",
    background: "#dcfce7",
  },
};