import axios from "axios";
export const api = axios.create({ baseURL: import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api/v1" });
export function setAccessToken(token){ if(token) api.defaults.headers.common.Authorization=`Bearer ${token}`; else delete api.defaults.headers.common.Authorization; }
api.interceptors.response.use(response=>response,error=>{if(error.response?.status===401){setAccessToken(null);if(typeof window!=="undefined"&&window.location.pathname!=="/login")window.location.assign("/login");}return Promise.reject(error);});
