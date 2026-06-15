import axiosClient from "./axiosClient.js";

export async function getUsers() {
    const response = await axiosClient.get("/users");
    return response.data.data;
}

export async function getUser(id) {
    const response = await axiosClient.get(`/users/${id}`);
    return response.data.data;
}

export async function updateUser(id, data) {
    const response = await axiosClient.put(`/users/${id}`, data);
    return response.data.data;
}

export async function updatePresence(status) {
    const response = await axiosClient.put("/users/me/presence", { status });
    return response.data;
}
