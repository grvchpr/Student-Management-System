import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { changePassword } from "../api/authApi";

const Profile = () => {
  const navigate = useNavigate();
  const { user, logout } = useAuth();

  const [form, setForm] = useState({
    currentPassword: "",
    newPassword: "",
    confirmPassword: "",
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const handleChange = (event) => {
    const { name, value } = event.target;

    setForm((current) => ({
      ...current,
      [name]: value,
    }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    setError("");
    setSuccess("");

    if (form.newPassword !== form.confirmPassword) {
      setError(
        "New password and confirm password do not match."
      );
      return;
    }

    if (form.newPassword.length < 8) {
      setError(
        "New password must be at least 8 characters."
      );
      return;
    }

    try {
      setLoading(true);

      await changePassword(form);

      setSuccess(
        "Password changed successfully. Please login again."
      );

      setForm({
        currentPassword: "",
        newPassword: "",
        confirmPassword: "",
      });

      setTimeout(() => {
        logout();
        window.location.href = "/login";
      }, 1500);
    } catch (err) {
      console.error(
        "Change password error:",
        err
      );

      if (err.response?.status === 400) {
        setError(
          err.response?.data?.message ||
            "Invalid password request."
        );
      } else if (err.response?.status === 401) {
        setError(
          "Your login session has expired. Please login again."
        );
      } else {
        setError(
          "Unable to change password. Please try again."
        );
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page-container">

      {/* Page Header */}
      <div className="page-header">

        <h1>My Profile</h1>

        <p>
          View your account information.
        </p>

        <button
          type="button"
          className="btn btn-secondary"
          onClick={() => navigate(-1)}
        >
          ← Back
        </button>

      </div>

      {/* Profile Information */}
      <div className="profile-card">

        <div className="profile-avatar">
          {(user?.username || "U")
            .charAt(0)
            .toUpperCase()}
        </div>

        <div className="profile-info">

          <div className="profile-field">
            <span className="profile-label">
              Username
            </span>

            <span className="profile-value">
              {user?.username || "-"}
            </span>
          </div>

          <div className="profile-field">
            <span className="profile-label">
              Role
            </span>

            <span className="profile-role">
              {user?.role || "USER"}
            </span>
          </div>

          <div className="profile-field">
            <span className="profile-label">
              Account Status
            </span>

            <span className="profile-status">
              APPROVED
            </span>
          </div>

        </div>
      </div>

      {/* Change Password */}
      <div className="content-card profile-password-card">

        <div className="content-card-header">
          <div>

            <h2>Change Password</h2>

            <p
              style={{
                marginTop: "5px",
                color: "#64748b",
                fontSize: "14px",
              }}
            >
              Update your account password securely.
            </p>

          </div>
        </div>

        {error && (
          <div className="alert alert-error">
            {error}
          </div>
        )}

        {success && (
          <div className="alert alert-success">
            {success}
          </div>
        )}

        <form
          className="profile-password-form"
          onSubmit={handleSubmit}
        >

          <div className="form-group">

            <label htmlFor="currentPassword">
              Current Password
            </label>

            <input
              id="currentPassword"
              name="currentPassword"
              type="password"
              value={form.currentPassword}
              onChange={handleChange}
              required
              autoComplete="current-password"
            />

          </div>

          <div className="form-group">

            <label htmlFor="newPassword">
              New Password
            </label>

            <input
              id="newPassword"
              name="newPassword"
              type="password"
              value={form.newPassword}
              onChange={handleChange}
              required
              minLength={8}
              autoComplete="new-password"
            />

            <small>
              Minimum 8 characters.
            </small>

          </div>

          <div className="form-group">

            <label htmlFor="confirmPassword">
              Confirm New Password
            </label>

            <input
              id="confirmPassword"
              name="confirmPassword"
              type="password"
              value={form.confirmPassword}
              onChange={handleChange}
              required
              minLength={8}
              autoComplete="new-password"
            />

          </div>

          <div className="profile-password-actions">

            <button
              type="submit"
              className="btn btn-primary"
              disabled={loading}
            >
              {loading
                ? "Changing Password..."
                : "Change Password"}
            </button>

          </div>

        </form>

      </div>

    </div>
  );
};

export default Profile;