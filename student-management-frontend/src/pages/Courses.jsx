import { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import {
  createCourse,
  deleteCourse,
  getCourseById,
  getCourses,
  updateCourse,
} from "../api/courseApi";

const emptyCourse = {
  courseName: "",
  courseCode: "",
  duration: "",
  fees: "",
  description: "",
};

const Courses = () => {
  const { user } = useAuth();
  const isAdmin = user?.role === "ADMIN";

  const [courses, setCourses] = useState([]);
  const [form, setForm] = useState(emptyCourse);

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const [editingId, setEditingId] = useState(null);

  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  const pageSize = 10;

  const loadCourses = async (page = currentPage) => {
    try {
      setLoading(true);
      setError("");

      const data = await getCourses(
        page,
        pageSize
      );

      setCourses(data.content ?? data);

      setCurrentPage(data.number ?? page);

      setTotalPages(data.totalPages ?? 0);

      setTotalElements(
        data.totalElements ?? 0
      );
    } catch (err) {
      console.error(
        "Load courses error:",
        err
      );

      if (err.response?.status === 401) {
        setError(
          "Your login session has expired. Please login again."
        );
      } else if (err.response?.status === 403) {
        setError(
          "You do not have permission to view courses."
        );
      } else {
        setError(
          "Unable to load courses."
        );
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadCourses(0);
  }, []);

  const handleChange = (event) => {
    const { name, value } = event.target;

    setForm((current) => ({
      ...current,
      [name]: value,
    }));
  };

  const handleEdit = async (id) => {
    try {
      setError("");
      setSuccess("");

      const course =
        await getCourseById(id);

      setForm({
        courseName:
          course.courseName ?? "",
        courseCode:
          course.courseCode ?? "",
        duration:
          course.duration ?? "",
        fees:
          course.fees ?? "",
        description:
          course.description ?? "",
      });

      setEditingId(id);

      window.scrollTo({
        top: 0,
        behavior: "smooth",
      });
    } catch (err) {
      console.error(
        "Load course error:",
        err
      );

      if (err.response?.status === 401) {
        setError(
          "Your login session has expired. Please login again."
        );
      } else if (err.response?.status === 403) {
        setError(
          "You do not have permission to edit courses."
        );
      } else if (err.response?.status === 404) {
        setError("Course not found.");
      } else {
        setError(
          "Unable to load course."
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

      const courseData = {
        ...form,
        fees: Number(form.fees),
      };

      if (editingId) {
        await updateCourse(
          editingId,
          courseData
        );

        setSuccess(
          "Course updated successfully."
        );
      } else {
        await createCourse(courseData);

        setSuccess(
          "Course created successfully."
        );
      }

      setForm(emptyCourse);
      setEditingId(null);

      await loadCourses(currentPage);
    } catch (err) {
      console.error(
        "Save course error:",
        err
      );

      if (err.response?.status === 403) {
        setError(
          "You do not have permission to modify courses."
        );
      } else if (err.response?.status === 404) {
        setError("Course not found.");
      } else if (err.response?.status === 409) {
        setError(
          err.response.data?.message ||
            "A course with this course code already exists."
        );
      } else if (err.response?.status === 400) {
        setError(
          err.response.data?.message ||
            "Invalid course data."
        );
      } else if (err.response?.status === 401) {
        setError(
          "Your login session has expired. Please login again."
        );
      } else {
        setError(
          err.response?.data?.message ||
            "Unable to save course."
        );
      }
    } finally {
      setSaving(false);
    }
  };

  const handleCancelEdit = () => {
    setForm(emptyCourse);
    setEditingId(null);
    setError("");
    setSuccess("");
  };

  const handleDelete = async (id) => {
    const confirmed = window.confirm(
      "Are you sure you want to delete this course?"
    );

    if (!confirmed) {
      return;
    }

    try {
      setError("");
      setSuccess("");

      await deleteCourse(id);

      setSuccess(
        "Course deleted successfully."
      );

      if (editingId === id) {
        setForm(emptyCourse);
        setEditingId(null);
      }

      let pageToLoad = currentPage;

      if (
        courses.length === 1 &&
        currentPage > 0
      ) {
        pageToLoad =
          currentPage - 1;
      }

      await loadCourses(pageToLoad);
    } catch (err) {
      console.error(
        "Delete course error:",
        err
      );

      if (err.response?.status === 403) {
        setError(
          "You do not have permission to delete courses."
        );
      } else if (err.response?.status === 404) {
        setError("Course not found.");
      } else if (err.response?.status === 409) {
        setError(
          err.response.data?.message ||
            "Course cannot be deleted because it has enrollments."
        );
      } else if (err.response?.status === 401) {
        setError(
          "Your login session has expired. Please login again."
        );
      } else {
        setError(
          err.response?.data?.message ||
            "Unable to delete course."
        );
      }
    }
  };

  if (loading) {
    return (
      <div className="page-container">
        <div className="loading-container">
          Loading courses...
        </div>
      </div>
    );
  }

  return (
    <div className="page-container">

      {/* Page Header */}
      <div className="page-header">
        <h1>Courses</h1>

        <p>
          Manage courses, course codes,
          duration and fees.
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
            ? "Edit Course"
            : "Add Course"}
        </h2>

        <form onSubmit={handleSubmit}>

          <div className="form-grid">

            {/* Course Name */}
            <div className="form-group">
              <label htmlFor="courseName">
                Course Name
              </label>

              <input
                id="courseName"
                name="courseName"
                type="text"
                value={form.courseName}
                onChange={handleChange}
                placeholder="e.g. Java Full Stack"
                required
              />
            </div>

            {/* Course Code */}
            <div className="form-group">
              <label htmlFor="courseCode">
                Course Code
              </label>

              <input
                id="courseCode"
                name="courseCode"
                type="text"
                value={form.courseCode}
                onChange={handleChange}
                placeholder="e.g. JFS001"
                required
              />
            </div>

            {/* Duration */}
            <div className="form-group">
              <label htmlFor="duration">
                Duration
              </label>

              <input
                id="duration"
                name="duration"
                type="text"
                value={form.duration}
                onChange={handleChange}
                placeholder="e.g. 6 Months"
                required
              />
            </div>

            {/* Fees */}
            <div className="form-group">
              <label htmlFor="fees">
                Fees
              </label>

              <input
                id="fees"
                name="fees"
                type="number"
                min="0"
                step="0.01"
                value={form.fees}
                onChange={handleChange}
                placeholder="e.g. 50000"
                required
              />
            </div>

            {/* Description */}
            <div className="form-group full-width">
              <label htmlFor="description">
                Description
              </label>

              <textarea
                id="description"
                name="description"
                value={form.description}
                onChange={handleChange}
                placeholder="Enter course description"
                rows="4"
              />
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
                ? "Update Course"
                : "Add Course"}
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

      {/* Course List */}
      <div className="content-card">

        <div className="content-card-header">
          <div>
            <h2>Course List</h2>

            <p
              style={{
                marginTop: "5px",
                color: "#64748b",
                fontSize: "14px",
              }}
            >
              Showing{" "}
              {courses.length} of{" "}
              {totalElements} courses
            </p>
          </div>
        </div>

        {courses.length === 0 ? (
          <div className="empty-state">
            No courses found.
          </div>
        ) : (
          <div className="table-container">

            <table>

              <thead>
                <tr>
                  <th>ID</th>
                  <th>Course Name</th>
                  <th>Course Code</th>
                  <th>Duration</th>
                  <th>Fees</th>
                  <th>Description</th>
                  {isAdmin && <th>Actions</th>}
                </tr>
              </thead>

              <tbody>
                {courses.map(
                  (course) => (
                    <tr key={course.id}>

                      <td>
                        {course.id}
                      </td>

                      <td>
                        {course.courseName}
                      </td>

                      <td>
                        <strong>
                          {course.courseCode}
                        </strong>
                      </td>

                      <td>
                        {course.duration}
                      </td>

                      <td>
                        ₹
                        {Number(
                          course.fees
                        ).toLocaleString(
                          "en-IN"
                        )}
                      </td>

                      <td>
                        {course.description ||
                          "-"}
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
                                course.id
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
                                course.id
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
              disabled={currentPage === 0}
              onClick={() =>
                loadCourses(
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
                loadCourses(
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

export default Courses;