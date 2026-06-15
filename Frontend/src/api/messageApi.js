import axiosClient from "./axiosClient.js";

export async function getMessages(channelId, pageable = { page: 0, size: 50 }) {
    const response = await axiosClient.get(`/channels/${channelId}/messages`, {
        params: pageable
    });
    return response.data.data;
}

export async function postMessage(channelId, content) {
    const response = await axiosClient.post(`/channels/${channelId}/messages`, {
        content
    });
    return response.data.data;
}

export async function editMessage(id, content) {
    const response = await axiosClient.put(`/messages/${id}`, { content });
    return response.data.data;
}

export async function deleteMessage(id) {
    const response = await axiosClient.delete(`/messages/${id}`);
    return response.data;
}

export async function getMessageHistory(id) {
    const response = await axiosClient.get(`/messages/${id}/history`);
    return response.data.data;
}
