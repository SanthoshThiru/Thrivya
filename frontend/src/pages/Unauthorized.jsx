import { Link } from "react-router-dom";
import "../styles/auth.css";

export default function Unauthorized() {
  return (
    <main className="auth-page">
      <section className="auth-card">
        <p className="brand">Thrivya</p>

        <h1>Access denied</h1>

        <p className="auth-description">
          You do not have permission to open this dashboard.
        </p>

        <Link
          to="/home"
          className="secondary-link"
        >
          Return to home
        </Link>
      </section>
    </main>
  );
}