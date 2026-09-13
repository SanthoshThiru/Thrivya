import { useState } from "react";
import {
  Link,
  useLocation,
  useNavigate,
} from "react-router-dom";
import { loginUser } from "../api/authApi";
import "../styles/auth.css";

export default function Login() {
  const navigate = useNavigate();
  const location = useLocation();

  const [formData, setFormData] = useState({
    email: "",
    password: "",
  });

  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const registrationMessage =
    location.state?.message || "";

  function handleChange(event) {
    const { name, value } = event.target;

    setFormData((previous) => ({
      ...previous,
      [name]: value,
    }));
  }

  function getDashboardPath(role) {
    switch (role) {
      case "STUDENT":
        return "/student";

      case "PLACEMENT_CELL":
        return "/placement";

      case "RECRUITER":
        return "/recruiter";

      case "TECH_INTERVIEWER":
        return "/interviewer";

      default:
        return null;
    }
  }

  async function handleSubmit(event) {
    event.preventDefault();

    setError("");
    setLoading(true);

    try {
      const result = await loginUser({
        email: formData.email.trim().toLowerCase(),
        password: formData.password,
      });

      console.log("Login response:", result);

      if (!result.token) {
        throw new Error(
          "Login succeeded, but no token was returned."
        );
      }

      localStorage.setItem(
        "thrivyaToken",
        result.token
      );

      localStorage.setItem(
        "thrivyaUser",
        JSON.stringify(result)
      );

      const dashboardPath = getDashboardPath(
        result.role
      );

      if (!dashboardPath) {
        throw new Error(
          `Unknown role received: ${result.role}`
        );
      }

      navigate(dashboardPath, {
        replace: true,
      });
    } catch (error) {
      const responseData = error.response?.data;

      let message =
        "Login failed. Check your email and password.";

      if (typeof responseData === "string") {
        message = responseData;
      } else if (responseData?.message) {
        message = responseData.message;
      }

      setError(message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="auth-page">
      <section className="auth-card">
        <p className="brand">Thrivya</p>

        <h1>Welcome back</h1>

        <p className="auth-description">
          Login to continue your career journey.
        </p>

        {registrationMessage && (
          <div className="success-message">
            {registrationMessage}
          </div>
        )}

        {error && (
          <div className="error-message">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="email">Email</label>

            <input
              id="email"
              name="email"
              type="email"
              value={formData.email}
              onChange={handleChange}
              placeholder="Enter your email"
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">Password</label>

            <input
              id="password"
              name="password"
              type="password"
              value={formData.password}
              onChange={handleChange}
              placeholder="Enter your password"
              required
            />
          </div>

          <button
            type="submit"
            className="primary-button"
            disabled={loading}
          >
            {loading ? "Logging in..." : "Login"}
          </button>
        </form>

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