import axiosClient from "./axiosClient.js";

export async function getResources() {
    const response = await axiosClient.get("/resources");
    return response.data.data;
}

export async function searchResources(keyword) {
    const response = await axiosClient.get("/resources/search", {
        params: { q: keyword }
    });
    return response.data.data;
}

export async function getResource(id) {
    const response = await axiosClient.get(`/resources/${id}`);
    return response.data.data;
}
