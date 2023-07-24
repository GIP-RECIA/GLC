import axios from "axios";

const { VITE_API_URL } = import.meta.env;

const instance = axios.create({
  baseURL: VITE_API_URL,
  withCredentials: true,
  xsrfCookieName: "CSRF-TOKEN",
  xsrfHeaderName: "X-CSRF-TOKEN",
});

export { instance };
