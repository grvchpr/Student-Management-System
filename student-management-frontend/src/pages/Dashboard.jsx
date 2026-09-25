import { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import { getDashboardStats } from "../api/dashboardApi";

const Dashboard = () => {
  const { user } = useAuth();

  const [stats, setStats] = useState({
    totalStudents: 0,
    totalCourses: 0,
    totalEnrollments: 0,
    pendingRegistrations: 0,
  });

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const isAdmin = user?.role === "ADMIN";

  const loadDashboardStats = async () => {
    try {
      setLoading(true);
      setError("");

      const data = await getDashboardStats();

      setStats({
        totalStudents: data.totalStudents ?? 0,
        totalCourses: data.totalCourses ?? 0,
        totalEnrollments: data.totalEnrollments ?? 0,
        pendingRegistrations:
          data.pendingRegistrations ?? 0,
      });
    } catch (err) {
      console.error(
        "Load dashboard stats error:",
        err
      );

      if (err.response?.status === 401) {
        setError(
          "Your login session has expired. Please login again."
        );
      } else if (err.response?.status === 403) {
        setError(
          "You do not have permission to view dashboard statistics."
        );
      } else {
        setError(
          err.response?.data?.message ||
            "Unable to load dashboard statistics."
        );
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (isAdmin) {
      loadDashboardStats();
    } else {
      setLoading(false);
    }
  }, [isAdmin]);

  if (loading) {
    return (
      <div className="page-container">
        <div className="loading-container">
          Loading dashboard...
        </div>
      </div>
    );
  }

  return (
    <div className="page-container">

      <div className="page-header">
        <h1>Dashboard</h1>

        <p>
          Welcome back,{" "}
          <strong>{user?.username || "User"}</strong>.
        </p>
      </div>

      {error && (
        <div className="alert alert-error">
          {error}
        </div>
      )}

      {isAdmin ? (
        <>
          <div className="dashboard-stats-grid">

            <div className="dashboard-stat-card">
              <div className="dashboard-stat-icon">
                👨‍🎓
              </div>

              <div className="dashboard-stat-content">
                <span className="dashboard-stat-label">
                  Total Students
                </span>

                <strong className="dashboard-stat-value">
                  {stats.totalStudents}
                </strong>
              </div>
            </div>

            <div className="dashboard-stat-card">
              <div className="dashboard-stat-icon">
                📚
              </div>

              <div className="dashboard-stat-content">
                <span className="dashboard-stat-label">
                  Total Courses
                </span>

                <strong className="dashboard-stat-value">
                  {stats.totalCourses}
                </strong>
              </div>
            </div>

            <div className="dashboard-stat-card">
              <div className="dashboard-stat-icon">
                📝
              </div>

              <div className="dashboard-stat-content">
                <span className="dashboard-stat-label">
                  Total Enrollments
                </span>

                <strong className="dashboard-stat-value">
                  {stats.totalEnrollments}
                </strong>
              </div>
            </div>

            <div className="dashboard-stat-card">
              <div className="dashboard-stat-icon">
                ⏳
              </div>

              <div className="dashboard-stat-content">
                <span className="dashboard-stat-label">
                  Pending Registrations
                </span>

                <strong className="dashboard-stat-value">
                  {stats.pendingRegistrations}
                </strong>
              </div>
            </div>

          </div>

          <div className="content-card">
            <div className="content-card-header">
              <div>
                <h2>Administration Overview</h2>

                <p
                  style={{
                    marginTop: "5px",
                    color: "#64748b",
                    fontSize: "14px",
                  }}
                >
                  Monitor students, courses,
                  enrollments and user registrations.
                </p>
              </div>
            </div>
          </div>
        </>
      ) : (
        <div className="content-card">
          <div className="content-card-header">
            <div>
              <h2>
                Welcome to Student Management System
              </h2>

              <p
                style={{
                  marginTop: "8px",
                  color: "#64748b",
                  fontSize: "14px",
                }}
              >
                You can browse available courses
                from the Courses section.
              </p>
            </div>
          </div>
        </div>
      )}

    </div>
  );
};

export default Dashboard;