import { useState } from "react";
import { useAuth } from "../context/AuthContext";

const Login = () => {
  const { login } = useAuth();

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (event) => {
    event.preventDefault();

    setError("");
    setLoading(true);

    try {
      await login(username, password);

      window.location.href = "/dashboard";
    } catch (err) {
      if (err.response?.status === 401) {
        setError(
          "Invalid username or password"
        );
      } else {
        setError(
          "Unable to connect to server"
        );
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page">

      <div className="login-container">

        {/* Brand */}
        <div className="login-brand">
          <div className="login-logo">
            SMS
          </div>

          <h1>
            Student Management System
          </h1>

          <p>
            Manage students, courses and
            enrollments in one place.
          </p>
        </div>

        {/* Login Card */}
        <div className="login-card">

          <div className="login-card-header">
            <h2>Welcome Back</h2>

            <p>
              Sign in to continue to your
              dashboard.
            </p>
          </div>

          {error && (
            <div className="alert alert-error">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit}>

            {/* Username */}
            <div className="form-group">
              <label htmlFor="username">
                Username
              </label>

              <input
                id="username"
                type="text"
                value={username}
                onChange={(event) =>
                  setUsername(
                    event.target.value
                  )
                }
                placeholder="Enter your username"
                autoComplete="username"
                required
                disabled={loading}
              />
            </div>

            <br />

            {/* Password */}
            <div className="form-group">
              <label htmlFor="password">
                Password
              </label>

              <input
                id="password"
                type="password"
                value={password}
                onChange={(event) =>
                  setPassword(
                    event.target.value
                  )
                }
                placeholder="Enter your password"
                autoComplete="current-password"
                required
                disabled={loading}
              />
            </div>

            <div className="login-actions">

              <button
                type="submit"
                className="btn btn-primary login-button"
                disabled={loading}
              >
                {loading
                  ? "Signing in..."
                  : "Sign In"}
              </button>

            </div>

          </form>

        </div>

        {/* Footer */}
        <div className="login-footer">
          Student Management System
        </div>

      </div>

    </div>
  );
};

export default Login;