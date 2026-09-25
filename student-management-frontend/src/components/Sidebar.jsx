import { NavLink } from "react-router-dom";

const Sidebar = () => {
  return (
    <aside className="sidebar">
      <div className="sidebar-logo">
        <h2>Student Management</h2>
      </div>

      <nav>
        <ul className="sidebar-menu">
          <li>
            <NavLink to="/dashboard">
              Dashboard
            </NavLink>
          </li>

          <li>
            <NavLink to="/students">
              Students
            </NavLink>
          </li>

          <li>
            <NavLink to="/courses">
              Courses
            </NavLink>
          </li>

          <li>
            <NavLink to="/enrollments">
              Enrollments
            </NavLink>
          </li>
        </ul>
      </nav>
    </aside>
  );
};

export default Sidebar;