import { createContext, useContext, useState } from "react";
import { loginUser } from "../api/authApi";

const AuthContext = createContext(null);

const getTokenPayload = (token) => {
  try {
    const payload = token.split(".")[1];

    const base64 = payload
      .replace(/-/g, "+")
      .replace(/_/g, "/");

    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split("")
        .map(
          (character) =>
            "%" +
            ("00" + character.charCodeAt(0).toString(16)).slice(-2)
        )
        .join("")
    );

    return JSON.parse(jsonPayload);
  } catch (error) {
    console.error("Unable to decode JWT:", error);
    return null;
  }
};

const getUserFromToken = (token) => {
  if (!token) {
    return null;
  }

  const payload = getTokenPayload(token);

  if (!payload) {
    return null;
  }

  let role =
    payload.role ||
    payload.roles ||
    payload.authorities ||
    payload.scope ||
    "";

  if (Array.isArray(role)) {
    role = role[0] || "";
  }

  if (typeof role === "string" && role.includes(" ")) {
    role = role.split(" ")[0];
  }

  role = String(role)
    .replace("ROLE_", "")
    .toUpperCase();

  return {
    username: payload.sub || payload.username || "",
    role,
  };
};

export const AuthProvider = ({ children }) => {
  const [token, setToken] = useState(
    () => localStorage.getItem("accessToken")
  );

  const [user, setUser] = useState(() => {
    const savedToken = localStorage.getItem("accessToken");

    return getUserFromToken(savedToken);
  });

  const login = async (username, password) => {
    const data = await loginUser(username, password);

    localStorage.setItem("accessToken", data.token);

    setToken(data.token);

    const loggedInUser = getUserFromToken(data.token);

    setUser(loggedInUser);

    return data;
  };

  const logout = () => {
    localStorage.removeItem("accessToken");

    setToken(null);
    setUser(null);
  };

  const isAuthenticated = Boolean(token);

  return (
    <AuthContext.Provider
      value={{
        token,
        user,
        isAuthenticated,
        login,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);

  if (!context) {
    throw new Error("useAuth must be used inside AuthProvider");
  }

  return context;
};