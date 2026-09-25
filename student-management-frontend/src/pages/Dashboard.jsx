import { useEffect, useState } from "react";
import { getStudents } from "../api/studentApi";
import { getCourses } from "../api/courseApi";
import { getEnrollments } from "../api/enrollmentApi";

const Dashboard = () => {
  const [studentsCount, setStudentsCount] = useState(0);
  const [coursesCount, setCoursesCount] = useState(0);
  const [enrollments, setEnrollments] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const loadDashboardData = async () => {
    try {
      setLoading(true);
      setError("");

      const [
        studentsData,
        coursesData,
        enrollmentData,
      ] = await Promise.all([
        getStudents(0, 1),
        getCourses(0, 1),
        getEnrollments(0, 100),
      ]);

      setStudentsCount(
        studentsData.totalElements ??
          studentsData.content?.length ??
          0
      );

      setCoursesCount(
        coursesData.totalElements ??
          coursesData.content?.length ??
          0
      );

      setEnrollments(
        enrollmentData.content ??
          enrollmentData
      );
    } catch (err) {
      console.error(
        "Dashboard loading error:",
        err
      );

      if (err.response?.status === 401) {
        setError(
          "Your login session has expired. Please login again."
        );
      } else if (err.response?.status === 403) {
        setError(
          "You do not have permission to view dashboard data."
        );
      } else {
        setError(
          "Unable to load dashboard data."
        );
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadDashboardData();
  }, []);

  const activeCount = enrollments.filter(
    (enrollment) =>
      enrollment.status === "ACTIVE"
  ).length;

  const completedCount = enrollments.filter(
    (enrollment) =>
      enrollment.status === "COMPLETED"
  ).length;

  const cancelledCount = enrollments.filter(
    (enrollment) =>
      enrollment.status === "CANCELLED"
  ).length;

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
          Welcome to the Student Management System.
        </p>
      </div>

      {error && (
        <div className="alert alert-error">
          {error}
        </div>
      )}

      <div className="dashboard-grid">
        <div className="stat-card">
          <div className="stat-card-header">
            <span className="stat-card-title">
              Total Students
            </span>

            <span className="stat-card-icon">
              S
            </span>
          </div>

          <div className="stat-card-value">
            {studentsCount}
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-card-header">
            <span className="stat-card-title">
              Total Courses
            </span>

            <span className="stat-card-icon">
              C
            </span>
          </div>

          <div className="stat-card-value">
            {coursesCount}
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-card-header">
            <span className="stat-card-title">
              Total Enrollments
            </span>

            <span className="stat-card-icon">
              E
            </span>
          </div>

          <div className="stat-card-value">
            {enrollments.length}
          </div>
        </div>
      </div>

      <div className="content-card">
        <div className="content-card-header">
          <h2>Enrollment Status</h2>
        </div>

        <div className="status-grid">
          <div className="status-card status-active">
            <div className="status-card-title">
              Active
            </div>

            <div className="status-card-value">
              {activeCount}
            </div>
          </div>

          <div className="status-card status-completed">
            <div className="status-card-title">
              Completed
            </div>

            <div className="status-card-value">
              {completedCount}
            </div>
          </div>

          <div className="status-card status-cancelled">
            <div className="status-card-title">
              Cancelled
            </div>

            <div className="status-card-value">
              {cancelledCount}
            </div>
          </div>
        </div>
      </div>

      <br />

      <div className="content-card">
        <div className="content-card-header">
          <h2>Recent Enrollments</h2>
        </div>

        {enrollments.length === 0 ? (
          <div className="empty-state">
            No enrollments found.
          </div>
        ) : (
          <div className="table-container">
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Student</th>
                  <th>Course</th>
                  <th>Date</th>
                  <th>Status</th>
                </tr>
              </thead>

              <tbody>
                {enrollments
                  .slice(0, 5)
                  .map((enrollment) => (
                    <tr key={enrollment.id}>
                      <td>
                        {enrollment.id}
                      </td>

                      <td>
                        {enrollment.studentName}
                      </td>

                      <td>
                        {enrollment.courseName}
                      </td>

                      <td>
                        {enrollment.enrollmentDate}
                      </td>

                      <td>
                        <span
                          className={`status-badge ${enrollment.status.toLowerCase()}`}
                        >
                          {enrollment.status}
                        </span>
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

export default Dashboard;