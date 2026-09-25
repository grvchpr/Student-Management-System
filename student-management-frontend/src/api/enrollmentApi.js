import axiosClient from "./axiosClient";

export const getEnrollments = async (page = 0, size = 10) => {
  const response = await axiosClient.get("/enrollments", {
    params: { page, size },
  });

  return response.data;
};

export const getEnrollmentById = async (id) => {
  const response = await axiosClient.get(`/enrollments/${id}`);

  return response.data;
};

export const getEnrollmentsByStudent = async (
  studentId,
  page = 0,
  size = 10
) => {
  const response = await axiosClient.get(
    `/enrollments/student/${studentId}`,
    {
      params: { page, size },
    }
  );

  return response.data;
};

export const getEnrollmentsByCourse = async (
  courseId,
  page = 0,
  size = 10
) => {
  const response = await axiosClient.get(
    `/enrollments/course/${courseId}`,
    {
      params: { page, size },
    }
  );

  return response.data;
};

export const createEnrollment = async (enrollment) => {
  const response = await axiosClient.post(
    "/enrollments",
    enrollment
  );

  return response.data;
};

export const updateEnrollmentStatus = async (
  id,
  status
) => {
  const response = await axiosClient.put(
    `/enrollments/${id}/status`,
    null,
    {
      params: {
        status,
      },
    }
  );

  return response.data;
};

export const deleteEnrollment = async (id) => {
  await axiosClient.delete(`/enrollments/${id}`);
};