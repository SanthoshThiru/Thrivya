import { useState } from "react";
import { useNavigate } from "react-router-dom";
import ResumeUpload from "./ResumeUpload";
import ManualSkills from "./ManualSkills";
import RoleSelection from "./RoleSelection";

export default function StudentProfileSetup() {
  const navigate = useNavigate();

  const [step, setStep] =
    useState("INPUT");

  return (
    <main style={styles.page}>
      <section style={styles.card}>
        <button
          onClick={() => navigate("/student")}
        >
          Back to dashboard
        </button>

        <h1>Set up your career profile</h1>

        <nav style={styles.steps}>
          <button
            onClick={() => setStep("INPUT")}
            style={
              step === "INPUT"
                ? styles.activeStep
                : styles.step
            }
          >
            1. Skills input
          </button>

          <button
            onClick={() => setStep("ROLE")}
            style={
              step === "ROLE"
                ? styles.activeStep
                : styles.step
            }
          >
            2. Choose role
          </button>
        </nav>

        {step === "INPUT" && (
          <section>
            <h2>Choose your input method</h2>

            <div style={styles.options}>
              <button
                onClick={() => setStep("RESUME")}
                style={styles.option}
              >
                Upload or update resume
              </button>

              <button
                onClick={() => setStep("MANUAL")}
                style={styles.option}
              >
                Enter or update skills manually
              </button>
            </div>
          </section>
        )}

        {step === "RESUME" && (
          <ResumeUpload
            embedded={true}
            onComplete={() => setStep("ROLE")}
          />
        )}

        {step === "MANUAL" && (
          <ManualSkills
            embedded={true}
            onComplete={() => setStep("ROLE")}
          />
        )}

        {step === "ROLE" && (
          <RoleSelection />
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
    maxWidth: "950px",
    margin: "0 auto",
    padding: "32px",
    background: "white",
    borderRadius: "18px",
  },
  steps: {
    display: "flex",
    gap: "10px",
    margin: "24px 0",
  },
  step: {
    padding: "10px 14px",
    border: "1px solid #cbd5e1",
    borderRadius: "8px",
    background: "white",
  },
  activeStep: {
    padding: "10px 14px",
    border: "none",
    borderRadius: "8px",
    background: "#2563eb",
    color: "white",
  },
  options: {
    display: "grid",
    gridTemplateColumns:
      "repeat(auto-fit, minmax(240px, 1fr))",
    gap: "16px",
  },
  option: {
    padding: "20px",
    border: "1px solid #bfdbfe",
    borderRadius: "10px",
    background: "#eff6ff",
    cursor: "pointer",
  },
};