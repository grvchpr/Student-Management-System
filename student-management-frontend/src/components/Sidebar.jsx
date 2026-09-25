import { NavLink } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const Sidebar = () => {
  const { user } = useAuth();
  const isAdmin = user?.role === "ADMIN";

  return (
    <aside className="sidebar">
      <div className="sidebar-logo">
        <h2>Student Management</h2>
      </div>

      <nav>
        <ul className="sidebar-menu">
          <li>
            <NavLink to="/dashboard">Dashboard</NavLink>
          </li>

          {isAdmin && (
            <li>
              <NavLink to="/students">Students</NavLink>
            </li>
          )}

          <li>
            <NavLink to="/courses">Courses</NavLink>
          </li>

          {isAdmin && (
            <li>
              <NavLink to="/enrollments">Enrollments</NavLink>
            </li>
          )}

          {isAdmin && (
            <li>
              <NavLink to="/user-registrations">User Registrations</NavLink>
            </li>
          )}
          <li>
            <NavLink to="/profile">My Profile</NavLink>
          </li>
        </ul>
      </nav>
    </aside>
  );
};

export default Sidebar;
