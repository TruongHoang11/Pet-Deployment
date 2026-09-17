import axios from "axios";

// ─────────────────────────────────────────────
// AXIOS INSTANCE
// ─────────────────────────────────────────────
const api = axios.create({
  baseURL: "/api/v1",
  timeout: 10000,
  headers: {
    "Content-Type": "application/json",
  },
});

const PUBLIC_GET_PREFIXES = [
  "/categories",
  "/services",
  "/menus",
  "/products",
  "/product-images",
  "/product-reviews",
  "/service-images",
  "/service-reviews",
];

const isPublicRequest = (config = {}) => {
  const method = (config.method || "get").toLowerCase();
  const url = config.url || "";

  return (
    method === "get" &&
    PUBLIC_GET_PREFIXES.some((prefix) =>
      url === prefix || url.startsWith(`${prefix}/`)
    )
  );
};

// ─────────────────────────────────────────────
// REQUEST INTERCEPTOR
// Tự động gắn JWT vào header
// ─────────────────────────────────────────────
api.interceptors.request.use(
  (config) => {
    const token =
      localStorage.getItem("petspa_token");

    console.log(
      "JWT TOKEN:",
      token
    );

    if (token && !isPublicRequest(config)) {
      config.headers.Authorization =
        `Bearer ${token}`;
    }

    return config;
  },
  (error) => Promise.reject(error)
);

// ─────────────────────────────────────────────
// RESPONSE INTERCEPTOR
// Xử lý lỗi tập trung
// ─────────────────────────────────────────────
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (
      error.response?.status === 401 &&
      !isPublicRequest(error.config)
    ) {
      console.error(
        "Token hết hạn hoặc không hợp lệ"
      );

      localStorage.removeItem(
        "petspa_token"
      );

      localStorage.removeItem(
        "petspa_user"
      );

      // Nếu muốn tự động về login
      window.dispatchEvent(new Event("auth:unauthorized"));
    }

    return Promise.reject(error);
  }
);

export default api;
