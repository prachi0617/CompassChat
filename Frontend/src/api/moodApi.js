import axiosClient from "./axiosClient.js";

export async function getMyMoods(pageable = { page: 0, size: 20 }) {
    const response = await axiosClient.get("/moods/me", {
        params: pageable
    });
    return response.data.data;
}

export async function logMood(moodType, note) {
    const response = await axiosClient.post("/moods", { moodType, note });
    return response.data.data;
}

export async function getMood(id) {
    const response = await axiosClient.get(`/moods/${id}`);
    return response.data.data;
}
