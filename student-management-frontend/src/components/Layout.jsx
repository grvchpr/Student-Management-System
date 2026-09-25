import { Outlet } from "react-router-dom";
import Navbar from "./Navbar";
import Sidebar from "./Sidebar";

const Layout = () => {
  return (
    <div className="app-layout">
      <Sidebar />

      <main className="main-content">
        <Navbar />

        <Outlet />
      </main>
    </div>
  );
};

export default Layout;