import { useState } from "react";
import { Link } from "react-router-dom";
import { registerUser } from "../api/authApi";

const Register = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] =
    useState("");

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (event) => {
    event.preventDefault();

    setError("");
    setSuccess("");

    const trimmedUsername =
      username.trim();

    /*
     * Username validation
     */
    if (!trimmedUsername) {
      setError("Username is required.");
      return;
    }

    if (trimmedUsername.length < 3) {
      setError(
        "Username must be at least 3 characters."
      );
      return;
    }

    if (trimmedUsername.length > 100) {
      setError(
        "Username cannot exceed 100 characters."
      );
      return;
    }

    /*
     * Password validation
     */
    if (password.length < 8) {
      setError(
        "Password must be at least 8 characters."
      );
      return;
    }

    if (password.length > 100) {
      setError(
        "Password cannot exceed 100 characters."
      );
      return;
    }

    /*
     * Confirm password validation
     */
    if (password !== confirmPassword) {
      setError(
        "Passwords do not match."
      );
      return;
    }

    setLoading(true);

    try {
      await registerUser(
        trimmedUsername,
        password
      );

      setSuccess(
        "Registration submitted successfully. " +
        "Please wait for administrator approval before logging in."
      );

      setUsername("");
      setPassword("");
      setConfirmPassword("");

    } catch (err) {
      console.error(
        "Registration error:",
        err
      );

      const status =
        err.response?.status;

      const serverMessage =
        err.response?.data?.message;

      if (status === 409) {
        setError(
          serverMessage ||
          "This username is already registered. Please choose another username."
        );
      } else if (status === 400) {
        setError(
          serverMessage ||
          "Please check the registration details and try again."
        );
      } else if (!err.response) {
        setError(
          "Unable to connect to the server. Please try again."
        );
      } else {
        setError(
          serverMessage ||
          "Unable to complete registration. Please try again."
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
            Create an account and wait for
            administrator approval.
          </p>

        </div>

        {/* Registration Card */}
        <div className="login-card">

          <div className="login-card-header">

            <h2>
              Create Account
            </h2>

            <p>
              Your account will be created
              as a USER.
            </p>

          </div>

          {/* Error */}
          {error && (
            <div className="alert alert-error">
              {error}
            </div>
          )}

          {/* Success */}
          {success && (
            <div className="alert alert-success">
              {success}
            </div>
          )}

          <form
            onSubmit={handleSubmit}
            noValidate
          >

            {/* Username */}
            <div className="form-group">

              <label htmlFor="register-username">
                Username
              </label>

              <input
                id="register-username"
                type="text"
                value={username}
                onChange={(event) =>
                  setUsername(
                    event.target.value
                  )
                }
                placeholder="Enter username"
                minLength={3}
                maxLength={100}
                autoComplete="username"
                required
                disabled={loading}
              />

            </div>

            <br />

            {/* Password */}
            <div className="form-group">

              <label htmlFor="register-password">
                Password
              </label>

              <input
                id="register-password"
                type="password"
                value={password}
                onChange={(event) =>
                  setPassword(
                    event.target.value
                  )
                }
                placeholder="Enter password"
                minLength={8}
                maxLength={100}
                autoComplete="new-password"
                required
                disabled={loading}
              />

              <small>
                Password must be at least
                8 characters.
              </small>

            </div>

            <br />

            {/* Confirm Password */}
            <div className="form-group">

              <label htmlFor="register-confirm-password">
                Confirm Password
              </label>

              <input
                id="register-confirm-password"
                type="password"
                value={confirmPassword}
                onChange={(event) =>
                  setConfirmPassword(
                    event.target.value
                  )
                }
                placeholder="Confirm password"
                minLength={8}
                maxLength={100}
                autoComplete="new-password"
                required
                disabled={loading}
              />

            </div>

            {/* Submit */}
            <div className="login-actions">

              <button
                type="submit"
                className="btn btn-primary login-button"
                disabled={loading}
              >
                {loading
                  ? "Registering..."
                  : "Register"}
              </button>

            </div>

          </form>

          <p
            style={{
              marginTop: "18px",
              textAlign: "center",
            }}
          >
            Already have an account?{" "}
            <Link to="/login">
              Login
            </Link>
          </p>

        </div>

      </div>

    </div>
  );
};

export default Register;