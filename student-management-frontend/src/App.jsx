import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import Login from "./pages/Login";
import Register from "./pages/Register";
import Dashboard from "./pages/Dashboard";
import ProtectedRoute from "./routes/ProtectedRoute";
import Layout from "./components/Layout";
import Students from "./pages/Students";
import Courses from "./pages/Courses";
import Enrollments from "./pages/Enrollments";
import UserRegistrations from "./pages/UserRegistrations";
import Profile from "./pages/Profile";

const App = () => {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/user-registrations" element={<UserRegistrations />} />
          <Route path="/profile" element={<Profile />} />

          <Route
            element={
              <ProtectedRoute>
                <Layout />
              </ProtectedRoute>
            }
          >
            <Route path="/dashboard" element={<Dashboard />} />
            <Route path="/courses" element={<Courses />} />

            <Route
              path="/students"
              element={
                <ProtectedRoute allowedRoles={["ADMIN"]}>
                  <Students />
                </ProtectedRoute>
              }
            />

            <Route
              path="/enrollments"
              element={
                <ProtectedRoute allowedRoles={["ADMIN"]}>
                  <Enrollments />
                </ProtectedRoute>
              }
            />

            <Route
              path="/user-registrations"
              element={
                <ProtectedRoute allowedRoles={["ADMIN"]}>
                  <UserRegistrations />
                </ProtectedRoute>
              }
            />
          </Route>

          <Route path="/" element={<Navigate to="/dashboard" replace />} />
          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
};

export default App;
