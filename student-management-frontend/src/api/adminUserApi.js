import axiosClient from "./axiosClient";

export const getPendingUsers = async () => {
  const response = await axiosClient.get(
    "/admin/users/pending"
  );

  return response.data;
};

export const approveUser = async (id) => {
  const response = await axiosClient.put(
    `/admin/users/${id}/approve`
  );

  return response.data;
};

export const rejectUser = async (id) => {
  const response = await axiosClient.put(
    `/admin/users/${id}/reject`
  );

  return response.data;
};