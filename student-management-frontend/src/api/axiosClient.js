import axios from "axios";

const axiosClient = axios.create({
  baseURL: "http://localhost:8080/api/v1",
  headers: {
    "Content-Type": "application/json",
  },
});

axiosClient.interceptors.request.use(
  (config) => {
    const token =
      localStorage.getItem("accessToken");

    if (token) {
      config.headers.Authorization =
        `Bearer ${token}`;
    }

    return config;
  },
  (error) =>
    Promise.reject(error)
);

axiosClient.interceptors.response.use(
  (response) => response,

  (error) => {
    const status =
      error.response?.status;

    const requestUrl =
      error.config?.url || "";

    /*
     * Do not treat failed login as an
     * expired-session event.
     *
     * Login 401 means invalid credentials.
     */
    const isLoginRequest =
      requestUrl.includes("/auth/login");

    if (
      status === 401 &&
      !isLoginRequest &&
      localStorage.getItem("accessToken")
    ) {
      localStorage.removeItem(
        "accessToken"
      );

      window.dispatchEvent(
        new Event(
          "auth:session-expired"
        )
      );
    }

    return Promise.reject(error);
  }
);

export default axiosClient;