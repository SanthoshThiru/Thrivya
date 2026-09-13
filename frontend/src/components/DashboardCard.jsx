import { useNavigate } from "react-router-dom";

export default function DashboardCard({
  title,
  role,
  email,
  message,
  children,
}) {
  const navigate = useNavigate();

  function handleLogout() {
    localStorage.removeItem("thrivyaToken");
    localStorage.removeItem("thrivyaUser");

    navigate("/login", {
      replace: true,
    });
  }

  return (
    <main style={styles.page}>
      <section style={styles.card}>
        <header style={styles.header}>
          <div>
            <p style={styles.brand}>Thrivya</p>
            <h1 style={styles.title}>{title}</h1>
          </div>

          <button
            type="button"
            style={styles.logoutButton}
            onClick={handleLogout}
          >
            Logout
          </button>
        </header>

        <p style={styles.role}>{role}</p>

        <p style={styles.message}>{message}</p>

        <p style={styles.emailText}>
          Logged in as: <strong>{email}</strong>
        </p>

        <div style={styles.content}>
          {children}
        </div>
      </section>
    </main>
  );
}

const styles = {
  page: {
    minHeight: "100vh",
    padding: "32px 20px",
    background: "#f4f7fb",
  },

  card: {
    width: "100%",
    maxWidth: "900px",
    margin: "0 auto",
    padding: "32px",
    borderRadius: "18px",
    background: "#ffffff",
    boxShadow: "0 10px 30px rgba(15, 23, 42, 0.12)",
  },

  header: {
    display: "flex",
    alignItems: "flex-start",
    justifyContent: "space-between",
    gap: "20px",
    marginBottom: "20px",
  },

  brand: {
    margin: "0 0 6px",
    color: "#2563eb",
    fontWeight: 700,
    fontSize: "18px",
  },

  title: {
    margin: 0,
    color: "#172033",
  },

  role: {
    display: "inline-block",
    margin: "0 0 16px",
    padding: "7px 12px",
    borderRadius: "20px",
    background: "#dbeafe",
    color: "#1d4ed8",
    fontWeight: 600,
  },

  message: {
    color: "#334155",
    fontSize: "16px",
  },

  emailText: {
    color: "#475569",
  },

  logoutButton: {
    flexShrink: 0,
    padding: "10px 16px",
    border: "none",
    borderRadius: "8px",
    background: "#ef4444",
    color: "#ffffff",
    fontSize: "14px",
    fontWeight: 600,
    cursor: "pointer",
  },

  content: {
    marginTop: "28px",
    paddingTop: "24px",
    borderTop: "1px solid #e2e8f0",
  },
};