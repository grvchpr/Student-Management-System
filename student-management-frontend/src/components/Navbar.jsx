import { useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const Navbar = () => {
  const location = useLocation();
  const navigate = useNavigate();

  const { user, logout } = useAuth();

  const pageTitles = {
    "/dashboard": "Dashboard",
    "/students": "Students",
    "/courses": "Courses",
    "/enrollments": "Enrollments",
  };

  const title =
    pageTitles[location.pathname] ||
    "Student Management System";

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  const displayUsername =
    user?.username || "User";

  const displayRole =
    user?.role || "USER";

  return (
    <header className="navbar">
      <div className="navbar-title">
        {title}
      </div>

      <div className="navbar-user">
        <div className="navbar-user-info">
          <span className="navbar-username">
            {displayUsername}
          </span>

          <span className="navbar-role">
            {displayRole}
          </span>
        </div>

        <button
          type="button"
          className="logout-button"
          onClick={handleLogout}
        >
          Logout
        </button>
      </div>
    </header>
  );
};

export default Navbar;