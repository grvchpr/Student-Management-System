import axiosClient from "./axiosClient";

export const loginUser = async (username, password) => {
  const response = await axiosClient.post("/auth/login", {
    username,
    password,
  });

  return response.data;
};