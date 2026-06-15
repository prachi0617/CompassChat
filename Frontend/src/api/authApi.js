import axiosClient from "./axiosClient.js";

export const registerUser = (userData) => {
    return axiosClient.post("/auth/register", userData);
};

export const loginUser = (loginData) => {
    return axiosClient.post("/auth/login", loginData);
};