export default function SkillList({
  skills = [],
}) {
  if (skills.length === 0) {
    return (
      <p>
        No skills have been added yet.
      </p>
    );
  }

  return (
    <div style={styles.container}>
      {skills.map((skill, index) => (
        <article
          key={`${skill.name}-${index}`}
          style={styles.card}
        >
          <div style={styles.row}>
            <strong>{skill.name}</strong>

            <span style={styles.source}>
              {skill.source}
            </span>
          </div>

          <p style={styles.confidence}>
            Confidence:{" "}
            {Math.round(
              (skill.confidence ?? 0) * 100
            )}
            %
          </p>

          <p style={styles.evidence}>
            Evidence: {skill.evidenceText}
          </p>
        </article>
      ))}
    </div>
  );
}

const styles = {
  container: {
    display: "grid",
    gap: "12px",
  },
  card: {
    padding: "14px",
    border: "1px solid #dbe3ef",
    borderRadius: "10px",
    background: "#f8fafc",
  },
  row: {
    display: "flex",
    justifyContent: "space-between",
    gap: "12px",
  },
  source: {
    color: "#2563eb",
    fontSize: "12px",
    fontWeight: 600,
  },
  confidence: {
    margin: "8px 0",
    color: "#475569",
    fontSize: "13px",
  },
  evidence: {
    margin: 0,
    color: "#64748b",
    fontSize: "13px",
  },
};