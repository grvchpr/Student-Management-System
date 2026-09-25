import { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import {
  createStudent,
  deleteStudent,
  getStudentById,
  getStudents,
  searchStudents,
  updateStudent,
} from "../api/studentApi";

const emptyStudent = {
  firstName: "",
  lastName: "",
  email: "",
  phone: "",
  dateOfBirth: "",
  address: "",
};

const Students = () => {
  const { user } = useAuth();
  const isAdmin = user?.role === "ADMIN";

  const [students, setStudents] = useState([]);
  const [form, setForm] = useState(emptyStudent);

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const [editingId, setEditingId] = useState(null);

  const [searchKeyword, setSearchKeyword] = useState("");

  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  const pageSize = 10;

  const loadStudents = async (
    page = currentPage,
    keyword = searchKeyword
  ) => {
    try {
      setLoading(true);
      setError("");

      const data = keyword.trim()
        ? await searchStudents(
            keyword.trim(),
            page,
            pageSize
          )
        : await getStudents(page, pageSize);

      setStudents(data.content ?? []);

      setCurrentPage(data.number ?? page);

      setTotalPages(data.totalPages ?? 0);

      setTotalElements(data.totalElements ?? 0);
    } catch (err) {
      console.error(
        "Load students error:",
        err
      );

      if (err.response?.status === 401) {
        setError(
          "Your login session has expired. Please login again."
        );
      } else if (err.response?.status === 403) {
        setError(
          "You do not have permission to view students."
        );
      } else {
        setError(
          "Unable to load students."
        );
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadStudents(0, "");
  }, []);

  const handleChange = (event) => {
    const { name, value } = event.target;

    setForm((current) => ({
      ...current,
      [name]: value,
    }));
  };

  const handleSearch = () => {
    setCurrentPage(0);
    loadStudents(0, searchKeyword);
  };

  const handleClearSearch = () => {
    setSearchKeyword("");
    setCurrentPage(0);
    loadStudents(0, "");
  };

  const handleEdit = async (id) => {
    try {
      setError("");
      setSuccess("");

      const student = await getStudentById(id);

      setForm({
        firstName: student.firstName ?? "",
        lastName: student.lastName ?? "",
        email: student.email ?? "",
        phone: student.phone ?? "",
        dateOfBirth:
          student.dateOfBirth ?? "",
        address: student.address ?? "",
      });

      setEditingId(id);

      window.scrollTo({
        top: 0,
        behavior: "smooth",
      });
    } catch (err) {
      console.error(
        "Load student error:",
        err
      );

      if (err.response?.status === 401) {
        setError(
          "Your login session has expired. Please login again."
        );
      } else if (err.response?.status === 403) {
        setError(
          "You do not have permission to edit students."
        );
      } else if (err.response?.status === 404) {
        setError("Student not found.");
      } else {
        setError(
          "Unable to load student."
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
        await updateStudent(
          editingId,
          form
        );

        setSuccess(
          "Student updated successfully."
        );
      } else {
        await createStudent(form);

        setSuccess(
          "Student created successfully."
        );
      }

      setForm(emptyStudent);
      setEditingId(null);

      await loadStudents(
        currentPage,
        searchKeyword
      );
    } catch (err) {
      console.error(
        "Student save error:",
        err
      );

      if (err.response?.status === 403) {
        setError(
          "You do not have permission to modify students."
        );
      } else if (err.response?.status === 409) {
        setError(
          err.response.data?.message ||
            "A student with this email already exists."
        );
      } else if (err.response?.status === 400) {
        setError(
          err.response.data?.message ||
            "Invalid student data."
        );
      } else if (err.response?.status === 401) {
        setError(
          "Your login session has expired. Please login again."
        );
      } else {
        setError(
          err.response?.data?.message ||
            "Unable to save student."
        );
      }
    } finally {
      setSaving(false);
    }
  };

  const handleCancelEdit = () => {
    setForm(emptyStudent);
    setEditingId(null);
    setError("");
    setSuccess("");
  };

  const handleDelete = async (id) => {
    const confirmed = window.confirm(
      "Are you sure you want to delete this student?"
    );

    if (!confirmed) {
      return;
    }

    try {
      setError("");
      setSuccess("");

      await deleteStudent(id);

      setSuccess(
        "Student deleted successfully."
      );

      if (editingId === id) {
        setForm(emptyStudent);
        setEditingId(null);
      }

      let pageToLoad = currentPage;

      if (
        students.length === 1 &&
        currentPage > 0
      ) {
        pageToLoad =
          currentPage - 1;
      }

      await loadStudents(
        pageToLoad,
        searchKeyword
      );
    } catch (err) {
      console.error(
        "Delete student error:",
        err
      );

      if (err.response?.status === 403) {
        setError(
          "You do not have permission to delete students."
        );
      } else if (err.response?.status === 409) {
        setError(
          err.response.data?.message ||
            "Student cannot be deleted because they have enrollments."
        );
      } else if (err.response?.status === 404) {
        setError("Student not found.");
      } else if (err.response?.status === 401) {
        setError(
          "Your login session has expired. Please login again."
        );
      } else {
        setError(
          err.response?.data?.message ||
            "Unable to delete student."
        );
      }
    }
  };

  if (loading) {
    return (
      <div className="page-container">
        <div className="loading-container">
          Loading students...
        </div>
      </div>
    );
  }

  return (
    <div className="page-container">

      {/* Page Header */}
      <div className="page-header">
        <h1>Students</h1>

        <p>
          Manage student information,
          registration and records.
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

      {/* Search */}
      <div className="filter-bar">
        <div className="filter-group">
          <label htmlFor="studentSearch">
            Search Students
          </label>

          <input
            id="studentSearch"
            type="text"
            placeholder="Search by name or email"
            value={searchKeyword}
            onChange={(event) =>
              setSearchKeyword(
                event.target.value
              )
            }
            onKeyDown={(event) => {
              if (event.key === "Enter") {
                handleSearch();
              }
            }}
          />
        </div>

        <button
          type="button"
          className="btn btn-primary"
          onClick={handleSearch}
        >
          Search
        </button>

        <button
          type="button"
          className="btn btn-secondary"
          onClick={handleClearSearch}
        >
          Clear
        </button>
      </div>

      {isAdmin && (
        <div className="form-card">

        <h2>
          {editingId
            ? "Edit Student"
            : "Add Student"}
        </h2>

        <form onSubmit={handleSubmit}>

          <div className="form-grid">

            {/* First Name */}
            <div className="form-group">
              <label htmlFor="firstName">
                First Name
              </label>

              <input
                id="firstName"
                name="firstName"
                type="text"
                value={form.firstName}
                onChange={handleChange}
                required
              />
            </div>

            {/* Last Name */}
            <div className="form-group">
              <label htmlFor="lastName">
                Last Name
              </label>

              <input
                id="lastName"
                name="lastName"
                type="text"
                value={form.lastName}
                onChange={handleChange}
                required
              />
            </div>

            {/* Email */}
            <div className="form-group">
              <label htmlFor="email">
                Email
              </label>

              <input
                id="email"
                name="email"
                type="email"
                value={form.email}
                onChange={handleChange}
                required
              />
            </div>

            {/* Phone */}
            <div className="form-group">
              <label htmlFor="phone">
                Phone
              </label>

              <input
                id="phone"
                name="phone"
                type="tel"
                value={form.phone}
                onChange={handleChange}
              />
            </div>

            {/* Date of Birth */}
            <div className="form-group">
              <label htmlFor="dateOfBirth">
                Date of Birth
              </label>

              <input
                id="dateOfBirth"
                name="dateOfBirth"
                type="date"
                value={form.dateOfBirth}
                onChange={handleChange}
              />
            </div>

            {/* Address */}
            <div className="form-group full-width">
              <label htmlFor="address">
                Address
              </label>

              <textarea
                id="address"
                name="address"
                value={form.address}
                onChange={handleChange}
                rows="3"
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
                ? "Update Student"
                : "Add Student"}
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

      {/* Student List */}
      <div className="content-card">

        <div className="content-card-header">
          <div>
            <h2>Student List</h2>

            <p
              style={{
                marginTop: "5px",
                color: "#64748b",
                fontSize: "14px",
              }}
            >
              Showing{" "}
              {students.length} of{" "}
              {totalElements} students
            </p>
          </div>
        </div>

        {students.length === 0 ? (
          <div className="empty-state">
            No students found.
          </div>
        ) : (
          <div className="table-container">

            <table>

              <thead>
                <tr>
                  <th>ID</th>
                  <th>First Name</th>
                  <th>Last Name</th>
                  <th>Email</th>
                  <th>Phone</th>
                  <th>Date of Birth</th>
                  {isAdmin && <th>Actions</th>}
                </tr>
              </thead>

              <tbody>
                {students.map(
                  (student) => (
                    <tr key={student.id}>

                      <td>
                        {student.id}
                      </td>

                      <td>
                        {student.firstName}
                      </td>

                      <td>
                        {student.lastName}
                      </td>

                      <td>
                        {student.email}
                      </td>

                      <td>
                        {student.phone || "-"}
                      </td>

                      <td>
                        {student.dateOfBirth ||
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
                                student.id
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
                                student.id
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
                loadStudents(
                  currentPage - 1,
                  searchKeyword
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
                loadStudents(
                  currentPage + 1,
                  searchKeyword
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

export default Students;