import axiosClient from "./axiosClient.js";

export async function getMyMentions() {
    const response = await axiosClient.get("/mentions/me");
    return response.data.data;
}

export async function getUnreadMentions() {
    const response = await axiosClient.get("/mentions/me/unread");
    return response.data.data;
}

export async function getUnreadMentionCount() {
    const response = await axiosClient.get("/mentions/me/unread/count");
    return response.data.data;
}

export async function markMentionRead(id) {
    const response = await axiosClient.post(`/mentions/${id}/read`);
    return response.data.data;
}
