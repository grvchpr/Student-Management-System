import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import {
  getPendingUsers,
  approveUser,
  rejectUser,
} from "../api/adminUserApi";

const UserRegistrations = () => {
  const navigate = useNavigate();

  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [processingId, setProcessingId] = useState(null);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const loadPendingUsers = async () => {
    try {
      setLoading(true);
      setError("");

      const data = await getPendingUsers();

      setUsers(data ?? []);
    } catch (err) {
      console.error(
        "Load pending users error:",
        err
      );

      if (err.response?.status === 401) {
        setError(
          "Your login session has expired. Please login again."
        );
      } else if (err.response?.status === 403) {
        setError(
          "You do not have permission to view user registrations."
        );
      } else {
        setError(
          err.response?.data?.message ||
            "Unable to load pending registrations."
        );
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadPendingUsers();
  }, []);

  const handleApprove = async (id) => {
    try {
      setProcessingId(id);
      setError("");
      setSuccess("");

      await approveUser(id);

      setSuccess(
        "User approved successfully."
      );

      await loadPendingUsers();
    } catch (err) {
      console.error(
        "Approve user error:",
        err
      );

      if (err.response?.status === 401) {
        setError(
          "Your login session has expired. Please login again."
        );
      } else if (err.response?.status === 403) {
        setError(
          "You do not have permission to approve users."
        );
      } else if (err.response?.status === 404) {
        setError("User registration not found.");
      } else {
        setError(
          err.response?.data?.message ||
            "Unable to approve user."
        );
      }
    } finally {
      setProcessingId(null);
    }
  };

  const handleReject = async (id) => {
    const confirmed = window.confirm(
      "Are you sure you want to reject this registration?"
    );

    if (!confirmed) {
      return;
    }

    try {
      setProcessingId(id);
      setError("");
      setSuccess("");

      await rejectUser(id);

      setSuccess(
        "User registration rejected successfully."
      );

      await loadPendingUsers();
    } catch (err) {
      console.error(
        "Reject user error:",
        err
      );

      if (err.response?.status === 401) {
        setError(
          "Your login session has expired. Please login again."
        );
      } else if (err.response?.status === 403) {
        setError(
          "You do not have permission to reject users."
        );
      } else if (err.response?.status === 404) {
        setError("User registration not found.");
      } else {
        setError(
          err.response?.data?.message ||
            "Unable to reject user."
        );
      }
    } finally {
      setProcessingId(null);
    }
  };

  if (loading) {
    return (
      <div className="page-container">

        <div className="page-header">

          <h1>User Registrations</h1>

          <p>
            Review and manage pending user registrations.
          </p>

          <button
            type="button"
            className="btn btn-secondary"
            onClick={() => navigate(-1)}
          >
            ← Back
          </button>

        </div>

        <div className="loading-container">
          Loading registrations...
        </div>

      </div>
    );
  }

  return (
    <div className="page-container">

      {/* Page Header */}
      <div className="page-header">

        <h1>User Registrations</h1>

        <p>
          Review and manage pending user registrations.
        </p>

        <button
          type="button"
          className="btn btn-secondary"
          onClick={() => navigate(-1)}
        >
          ← Back
        </button>

      </div>

      {/* Alerts */}
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

      {/* Registration List */}
      <div className="content-card">

        <div className="content-card-header">

          <div>

            <h2>
              Pending Registrations
            </h2>

            <p
              style={{
                marginTop: "5px",
                color: "#64748b",
                fontSize: "14px",
              }}
            >
              {users.length} pending registration
              {users.length === 1 ? "" : "s"}.
            </p>

          </div>

        </div>

        {users.length === 0 ? (

          <div className="empty-state">
            No pending registrations found.
          </div>

        ) : (

          <div className="table-container">

            <table>

              <thead>
                <tr>
                  <th>ID</th>
                  <th>Username</th>
                  <th>Role</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>

              <tbody>

                {users.map((user) => (

                  <tr key={user.id}>

                    <td>
                      {user.id}
                    </td>

                    <td>
                      <strong>
                        {user.username}
                      </strong>
                    </td>

                    <td>
                      {user.role}
                    </td>

                    <td>
                      <span className="status-badge pending">
                        {user.status}
                      </span>
                    </td>

                    <td>

                      <div
                        style={{
                          display: "flex",
                          gap: "8px",
                        }}
                      >

                        <button
                          type="button"
                          className="btn btn-primary"
                          onClick={() =>
                            handleApprove(user.id)
                          }
                          disabled={
                            processingId === user.id
                          }
                        >
                          {processingId === user.id
                            ? "Processing..."
                            : "Approve"}
                        </button>

                        <button
                          type="button"
                          className="btn btn-danger"
                          onClick={() =>
                            handleReject(user.id)
                          }
                          disabled={
                            processingId === user.id
                          }
                        >
                          Reject
                        </button>

                      </div>

                    </td>

                  </tr>

                ))}

              </tbody>

            </table>

          </div>

        )}

      </div>

    </div>
  );
};

export default UserRegistrations;