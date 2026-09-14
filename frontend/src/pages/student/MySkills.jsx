import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getStudentProfile } from "../../api/studentApi";
import SkillList from "../../components/SkillList";

export default function MySkills() {
  const navigate = useNavigate();

  const [profile, setProfile] =
    useState(null);

  const [error, setError] =
    useState("");

  useEffect(() => {
    async function loadProfile() {
      try {
        const result =
          await getStudentProfile();

        setProfile(result);
      } catch (requestError) {
        setError(
          requestError.response?.data?.message ||
          "Unable to load your skills"
        );
      }
    }

    loadProfile();
  }, []);

  return (
    <main style={styles.page}>
      <section style={styles.card}>
        <button
          onClick={() => navigate("/student")}
        >
          Back to dashboard
        </button>

        <h1>My skill profile</h1>

        {error && (
          <p style={styles.error}>{error}</p>
        )}

        {!profile && !error && (
          <p>Loading your skills...</p>
        )}

        {profile && (
          <>
            <p>
              Input mode:{" "}
              <strong>{profile.inputMode}</strong>
            </p>

            {profile.selectedRoleTitle && (
              <p>
                Selected role:{" "}
                <strong>
                  {profile.selectedRoleTitle}
                </strong>
              </p>
            )}

            <h2>Current skills</h2>

            <SkillList
              skills={profile.skills || []}
            />
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
  error: {
    padding: "10px",
    color: "#b91c1c",
    background: "#fee2e2",
  },
};