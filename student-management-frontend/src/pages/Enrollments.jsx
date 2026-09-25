import { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import {
  createEnrollment,
  deleteEnrollment,
  getEnrollmentById,
  getEnrollments,
  getEnrollmentsByCourse,
  getEnrollmentsByStudent,
  updateEnrollmentStatus,
} from "../api/enrollmentApi";
import { getStudents } from "../api/studentApi";
import { getCourses } from "../api/courseApi";

const emptyEnrollment = {
  studentId: "",
  courseId: "",
  enrollmentDate: "",
  status: "ACTIVE",
};

const Enrollments = () => {
  const { user } = useAuth();
  const isAdmin = user?.role === "ADMIN";

  const [enrollments, setEnrollments] = useState([]);
  const [students, setStudents] = useState([]);
  const [courses, setCourses] = useState([]);

  const [form, setForm] = useState(emptyEnrollment);

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const [editingId, setEditingId] = useState(null);

  const [selectedStudentId, setSelectedStudentId] =
    useState("");

  const [selectedCourseId, setSelectedCourseId] =
    useState("");

  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  const pageSize = 10;

  const loadStudentsAndCourses = async () => {
    try {
      const [studentData, courseData] =
        await Promise.all([
          getStudents(0, 100),
          getCourses(0, 100),
        ]);

      setStudents(
        studentData.content ?? studentData
      );

      setCourses(
        courseData.content ?? courseData
      );
    } catch (err) {
      console.error(
        "Load students/courses error:",
        err
      );

      if (err.response?.status === 401) {
        setError(
          "Your login session has expired. Please login again."
        );
      } else {
        setError(
          "Unable to load students and courses."
        );
      }
    }
  };

  const loadEnrollments = async (
    page = 0,
    studentId = selectedStudentId,
    courseId = selectedCourseId
  ) => {
    try {
      setLoading(true);
      setError("");

      let data;

      if (studentId) {
        data = await getEnrollmentsByStudent(
          studentId,
          page,
          pageSize
        );
      } else if (courseId) {
        data = await getEnrollmentsByCourse(
          courseId,
          page,
          pageSize
        );
      } else {
        data = await getEnrollments(
          page,
          pageSize
        );
      }

      setEnrollments(data.content ?? data);

      setCurrentPage(data.number ?? page);

      setTotalPages(data.totalPages ?? 0);

      setTotalElements(
        data.totalElements ?? 0
      );
    } catch (err) {
      console.error(
        "Load enrollments error:",
        err
      );

      if (err.response?.status === 401) {
        setError(
          "Your login session has expired. Please login again."
        );
      } else if (err.response?.status === 403) {
        setError(
          "You do not have permission to view enrollments."
        );
      } else if (err.response?.status === 404) {
        setError(
          "The selected student or course was not found."
        );
      } else {
        setError(
          "Unable to load enrollments."
        );
      }
    } finally {
      setLoading(false);
    }
  };

  const loadData = async () => {
    await Promise.all([
      loadStudentsAndCourses(),
      loadEnrollments(0, "", ""),
    ]);
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleChange = (event) => {
    const { name, value } = event.target;

    setForm((current) => ({
      ...current,
      [name]: value,
    }));
  };

  const handleStudentFilterChange = (event) => {
    const value = event.target.value;

    setSelectedStudentId(value);
    setSelectedCourseId("");

    loadEnrollments(0, value, "");
  };

  const handleCourseFilterChange = (event) => {
    const value = event.target.value;

    setSelectedCourseId(value);
    setSelectedStudentId("");

    loadEnrollments(0, "", value);
  };

  const handleClearFilters = () => {
    setSelectedStudentId("");
    setSelectedCourseId("");

    loadEnrollments(0, "", "");
  };

  const handlePageChange = (page) => {
    loadEnrollments(
      page,
      selectedStudentId,
      selectedCourseId
    );
  };

  const handleEdit = async (id) => {
    try {
      setError("");
      setSuccess("");

      const enrollment =
        await getEnrollmentById(id);

      setForm({
        studentId:
          enrollment.studentId ?? "",
        courseId:
          enrollment.courseId ?? "",
        enrollmentDate:
          enrollment.enrollmentDate ?? "",
        status:
          enrollment.status ?? "ACTIVE",
      });

      setEditingId(id);

      window.scrollTo({
        top: 0,
        behavior: "smooth",
      });
    } catch (err) {
      console.error(
        "Load enrollment error:",
        err
      );

      if (err.response?.status === 401) {
        setError(
          "Your login session has expired. Please login again."
        );
      } else if (err.response?.status === 403) {
        setError(
          "You do not have permission to view this enrollment."
        );
      } else if (err.response?.status === 404) {
        setError("Enrollment not found.");
      } else {
        setError(
          "Unable to load enrollment."
        );
      }
    }
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    try {
      setSaving(true);
      setError("");
      setSuccess("");

      if (editingId) {
        await updateEnrollmentStatus(
          editingId,
          form.status
        );

        setSuccess(
          "Enrollment status updated successfully."
        );
      } else {
        await createEnrollment({
          studentId: Number(form.studentId),
          courseId: Number(form.courseId),
          enrollmentDate:
            form.enrollmentDate,
          status: form.status,
        });

        setSuccess(
          "Enrollment created successfully."
        );
      }

      setForm(emptyEnrollment);
      setEditingId(null);

      await loadEnrollments(
        currentPage,
        selectedStudentId,
        selectedCourseId
      );
    } catch (err) {
      console.error(
        "Save enrollment error:",
        err
      );

      if (err.response?.status === 403) {
        setError(
          "You do not have permission to modify enrollments."
        );
      } else if (err.response?.status === 404) {
        setError("Enrollment not found.");
      } else if (err.response?.status === 409) {
        setError(
          err.response.data?.message ||
            "This student is already enrolled in this course."
        );
      } else if (err.response?.status === 400) {
        setError(
          err.response.data?.message ||
            "Invalid enrollment data."
        );
      } else if (err.response?.status === 401) {
        setError(
          "Your login session has expired. Please login again."
        );
      } else {
        setError(
          err.response?.data?.message ||
            "Unable to save enrollment."
        );
      }
    } finally {
      setSaving(false);
    }
  };

  const handleCancelEdit = () => {
    setForm(emptyEnrollment);
    setEditingId(null);
    setError("");
    setSuccess("");
  };

  const handleDelete = async (id) => {
    const confirmed = window.confirm(
      "Are you sure you want to delete this enrollment?"
    );

    if (!confirmed) {
      return;
    }

    try {
      setError("");
      setSuccess("");

      await deleteEnrollment(id);

      setSuccess(
        "Enrollment deleted successfully."
      );

      if (editingId === id) {
        setForm(emptyEnrollment);
        setEditingId(null);
      }

      let pageToLoad = currentPage;

      if (
        enrollments.length === 1 &&
        currentPage > 0
      ) {
        pageToLoad =
          currentPage - 1;
      }

      await loadEnrollments(
        pageToLoad,
        selectedStudentId,
        selectedCourseId
      );
    } catch (err) {
      console.error(
        "Delete enrollment error:",
        err
      );

      if (err.response?.status === 401) {
        setError(
          "Your login session has expired. Please login again."
        );
      } else if (err.response?.status === 403) {
        setError(
          "You do not have permission to delete enrollments."
        );
      } else if (err.response?.status === 404) {
        setError("Enrollment not found.");
      } else {
        setError(
          err.response?.data?.message ||
            "Unable to delete enrollment."
        );
      }
    }
  };

  if (loading) {
    return (
      <div className="page-container">
        <div className="loading-container">
          Loading enrollments...
        </div>
      </div>
    );
  }

  return (
    <div className="page-container">

      {/* Page Header */}
      <div className="page-header">
        <h1>Enrollments</h1>

        <p>
          Manage student course enrollments
          and enrollment status.
        </p>
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

      {isAdmin && (
        <div className="form-card">

        <h2>
          {editingId
            ? "Edit Enrollment Status"
            : "Create Enrollment"}
        </h2>

        <form onSubmit={handleSubmit}>

          <div className="form-grid">

            {/* Student */}
            <div className="form-group">
              <label htmlFor="studentId">
                Student
              </label>

              <select
                id="studentId"
                name="studentId"
                value={form.studentId}
                onChange={handleChange}
                required
                disabled={Boolean(
                  editingId
                )}
              >
                <option value="">
                  Select Student
                </option>

                {students.map(
                  (student) => (
                    <option
                      key={student.id}
                      value={student.id}
                    >
                      {student.firstName}{" "}
                      {student.lastName} -{" "}
                      {student.email}
                    </option>
                  )
                )}
              </select>
            </div>

            {/* Course */}
            <div className="form-group">
              <label htmlFor="courseId">
                Course
              </label>

              <select
                id="courseId"
                name="courseId"
                value={form.courseId}
                onChange={handleChange}
                required
                disabled={Boolean(
                  editingId
                )}
              >
                <option value="">
                  Select Course
                </option>

                {courses.map(
                  (course) => (
                    <option
                      key={course.id}
                      value={course.id}
                    >
                      {course.courseName} (
                      {course.courseCode})
                    </option>
                  )
                )}
              </select>
            </div>

            {/* Enrollment Date */}
            <div className="form-group">
              <label htmlFor="enrollmentDate">
                Enrollment Date
              </label>

              <input
                id="enrollmentDate"
                name="enrollmentDate"
                type="date"
                value={
                  form.enrollmentDate
                }
                onChange={handleChange}
                required
                disabled={Boolean(
                  editingId
                )}
              />
            </div>

            {/* Status */}
            <div className="form-group">
              <label htmlFor="status">
                Status
              </label>

              <select
                id="status"
                name="status"
                value={form.status}
                onChange={handleChange}
                required
              >
                <option value="ACTIVE">
                  ACTIVE
                </option>

                <option value="COMPLETED">
                  COMPLETED
                </option>

                <option value="CANCELLED">
                  CANCELLED
                </option>
              </select>
            </div>

          </div>

          {/* Form Actions */}
          <div className="form-actions">

            <button
              type="submit"
              className="btn btn-primary"
              disabled={saving}
            >
              {saving
                ? "Saving..."
                : editingId
                ? "Update Status"
                : "Create Enrollment"}
            </button>

            {editingId && (
              <button
                type="button"
                className="btn btn-secondary"
                onClick={handleCancelEdit}
                disabled={saving}
              >
                Cancel
              </button>
            )}

          </div>

        </form>

        </div>
      )}

      {/* Enrollment List */}
      <div className="content-card">

        <div className="content-card-header">
          <div>
            <h2>Enrollment List</h2>

            <p
              style={{
                marginTop: "5px",
                color: "#64748b",
                fontSize: "14px",
              }}
            >
              Showing{" "}
              {enrollments.length} of{" "}
              {totalElements} enrollments
            </p>
          </div>
        </div>

        {/* Filters */}
        <div className="filter-bar">

          <div className="filter-group">
            <label htmlFor="studentFilter">
              Student
            </label>

            <select
              id="studentFilter"
              value={selectedStudentId}
              onChange={
                handleStudentFilterChange
              }
            >
              <option value="">
                All Students
              </option>

              {students.map(
                (student) => (
                  <option
                    key={student.id}
                    value={student.id}
                  >
                    {student.firstName}{" "}
                    {student.lastName}
                  </option>
                )
              )}
            </select>
          </div>

          <div className="filter-group">
            <label htmlFor="courseFilter">
              Course
            </label>

            <select
              id="courseFilter"
              value={selectedCourseId}
              onChange={
                handleCourseFilterChange
              }
            >
              <option value="">
                All Courses
              </option>

              {courses.map(
                (course) => (
                  <option
                    key={course.id}
                    value={course.id}
                  >
                    {course.courseName} (
                    {course.courseCode})
                  </option>
                )
              )}
            </select>
          </div>

          <button
            type="button"
            className="btn btn-secondary"
            onClick={handleClearFilters}
          >
            Clear Filters
          </button>

        </div>

        {/* Table */}
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
                  <th>Course Code</th>
                  <th>Enrollment Date</th>
                  <th>Status</th>
                  {isAdmin && <th>Actions</th>}
                </tr>
              </thead>

              <tbody>
                {enrollments.map(
                  (enrollment) => (
                    <tr
                      key={
                        enrollment.id
                      }
                    >

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
                        <strong>
                          {
                            enrollment.courseCode
                          }
                        </strong>
                      </td>

                      <td>
                        {
                          enrollment.enrollmentDate
                        }
                      </td>

                      <td>
                        <span
                          className={`status-badge ${enrollment.status.toLowerCase()}`}
                        >
                          {
                            enrollment.status
                          }
                        </span>
                      </td>

                      {isAdmin && (
                      <td>
                        <div
                          style={{
                            display: "flex",
                            gap: "8px",
                          }}
                        >
                          <button
                            type="button"
                            className="btn btn-secondary"
                            onClick={() =>
                              handleEdit(
                                enrollment.id
                              )
                            }
                          >
                            Edit
                          </button>

                          <button
                            type="button"
                            className="btn btn-danger"
                            onClick={() =>
                              handleDelete(
                                enrollment.id
                              )
                            }
                          >
                            Delete
                          </button>
                        </div>
                      </td>
                      )}

                    </tr>
                  )
                )}
              </tbody>

            </table>

          </div>
        )}

        {/* Pagination */}
        {totalPages > 0 && (
          <div className="pagination">

            <button
              type="button"
              className="btn btn-secondary"
              disabled={
                currentPage === 0
              }
              onClick={() =>
                handlePageChange(
                  currentPage - 1
                )
              }
            >
              Previous
            </button>

            <span className="pagination-info">
              Page{" "}
              {currentPage + 1} of{" "}
              {totalPages}
            </span>

            <button
              type="button"
              className="btn btn-secondary"
              disabled={
                currentPage >=
                totalPages - 1
              }
              onClick={() =>
                handlePageChange(
                  currentPage + 1
                )
              }
            >
              Next
            </button>

          </div>
        )}

      </div>
    </div>
  );
};

export default Enrollments;