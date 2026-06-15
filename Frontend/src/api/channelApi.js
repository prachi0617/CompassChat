import axiosClient from "./axiosClient.js";

export async function getChannels() {
    const response = await axiosClient.get("/channels");
    return response.data.data;
}

export async function getMyChannels() {
    const response = await axiosClient.get("/channels/mine");
    return response.data.data;
}

export async function getChannel(id) {
    const response = await axiosClient.get(`/channels/${id}`);
    return response.data.data;
}

export async function createChannel(data) {
    const response = await axiosClient.post("/channels", data);
    return response.data.data;
}

export async function updateChannel(id, data) {
    const response = await axiosClient.put(`/channels/${id}`, data);
    return response.data.data;
}

export async function archiveChannel(id) {
    const response = await axiosClient.post(`/channels/${id}/archive`);
    return response.data;
}

export async function getMembers(id) {
    const response = await axiosClient.get(`/channels/${id}/members`);
    return response.data.data;
}

export async function addMember(id, userId) {
    const response = await axiosClient.post(`/channels/${id}/members`, { userId });
    return response.data.data;
}

export async function removeMember(id, userId) {
    const response = await axiosClient.delete(`/channels/${id}/members/${userId}`);
    return response.data;
}

export async function markRead(id) {
    const response = await axiosClient.post(`/channels/${id}/read`);
    return response.data;
}

export async function getUnreadCount(id) {
    const response = await axiosClient.get(`/channels/${id}/unread`);
    return response.data.data;
}
