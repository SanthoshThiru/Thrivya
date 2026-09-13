import { Link } from "react-router-dom";
import "../styles/auth.css";

export default function Home() {
  return (
    <main className="auth-page">
      <section className="auth-card">
        <p className="brand">Thrivya</p>

        <h1>Start your journey with Thrivya</h1>

        <p className="auth-description">
          Build your skills, improve your resume, and move
          toward your career goals with explainable career
          intelligence.
        </p>

        <Link
          to="/login"
          className="primary-button"
          style={{
            display: "block",
            textAlign: "center",
            textDecoration: "none",
          }}
        >
          Login
        </Link>

        <p className="auth-footer">
          New user?{" "}
          <Link
            to="/register"
            className="secondary-link"
          >
            Register here
          </Link>
        </p>
      </section>
    </main>
  );
}