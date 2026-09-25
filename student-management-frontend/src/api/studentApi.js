import axiosClient from "./axiosClient";

export const getStudents = async (page = 0, size = 10) => {
  const response = await axiosClient.get("/students", {
    params: {
      page,
      size,
    },
  });

  return response.data;
};

export const getStudentById = async (id) => {
  const response = await axiosClient.get(`/students/${id}`);

  return response.data;
};

export const createStudent = async (student) => {
  const response = await axiosClient.post("/students", student);

  return response.data;
};

export const updateStudent = async (id, student) => {
  const response = await axiosClient.put(`/students/${id}`, student);

  return response.data;
};

export const deleteStudent = async (id) => {
  await axiosClient.delete(`/students/${id}`);
};

export const searchStudents = async (keyword, page = 0, size = 10) => {
  const response = await axiosClient.get("/students/search", {
    params: {
      name: keyword,
      page,
      size,
    },
  });

  return response.data;
};
