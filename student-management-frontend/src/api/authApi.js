import axiosClient from "./axiosClient";

export const loginUser = async (username, password) => {
  const response = await axiosClient.post("/auth/login", {
    username,
    password,
  });

  return response.data;
};

export const changePassword = async (passwordData) => {
  const response = await axiosClient.post(
    "/auth/change-password",
    passwordData
  );

  return response.data;
};

export const registerUser = async (username, password) => {
  const response = await axiosClient.post("/auth/register", {
    username,
    password,
  });
  return response.data;
};