import { useState } from "react";
import {
  Link,
  useNavigate,
} from "react-router-dom";

import { registerUser } from "../api/authApi";
import "../styles/auth.css";

export default function Register() {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    userName: "",
    email: "",
    password: "",
    role: "STUDENT",
  });

  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  function handleChange(event) {
    const { name, value } = event.target;

    setFormData((previous) => ({
      ...previous,
      [name]: value,
    }));
  }

  async function handleSubmit(event) {
    event.preventDefault();

    setError("");

    if (formData.password.length < 8) {
      setError(
        "Password must contain at least 8 characters."
      );
      return;
    }

    setLoading(true);

    try {
      const payload = {
        userName: formData.userName
          .trim(),

        email: formData.email
          .trim()
          .toLowerCase(),

        password: formData.password,

        role: formData.role,
      };

      await registerUser(payload);

      navigate("/login", {
        replace: true,
        state: {
          message:
            "Registration successful. Please log in.",
        },
      });
    } catch (error) {
      console.error(
        "Registration error:",
        error
      );

      const responseData =
        error.response?.data;

      let message =
        "Registration failed. Please try again.";

      if (typeof responseData === "string") {
        message = responseData;
      } else if (responseData?.message) {
        message = responseData.message;
      } else if (responseData) {
        const firstValidationError =
          Object.values(responseData)[0];

        if (firstValidationError) {
          message = firstValidationError;
        }
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

        <h1>Create your account</h1>

        <p className="auth-description">
          Register to begin your personalized career
          journey.
        </p>

        {error && (
          <div className="error-message">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="userName">
              Full name
            </label>

            <input
              id="userName"
              name="userName"
              type="text"
              value={formData.userName}
              onChange={handleChange}
              placeholder="Enter your full name"
              autoComplete="name"
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="email">
              Email
            </label>

            <input
              id="email"
              name="email"
              type="email"
              value={formData.email}
              onChange={handleChange}
              placeholder="Enter your email"
              autoComplete="email"
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">
              Password
            </label>

            <input
              id="password"
              name="password"
              type="password"
              value={formData.password}
              onChange={handleChange}
              placeholder="Minimum 8 characters"
              autoComplete="new-password"
              minLength={8}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="role">
              Account type
            </label>

            <select
              id="role"
              name="role"
              value={formData.role}
              onChange={handleChange}
              required
            >
              <option value="STUDENT">
                Student
              </option>

              <option value="PLACEMENT_CELL">
                College Placement Cell
              </option>

              <option value="RECRUITER">
                Recruiter
              </option>

              <option value="TECH_INTERVIEWER">
                Technical Interviewer
              </option>
            </select>
          </div>

          <button
            type="submit"
            className="primary-button"
            disabled={loading}
          >
            {loading
              ? "Creating account..."
              : "Register"}
          </button>
        </form>

        <p className="auth-footer">
          Already have an account?{" "}
          <Link
            to="/login"
            className="secondary-link"
          >
            Login here
          </Link>
        </p>
      </section>
    </main>
  );
}