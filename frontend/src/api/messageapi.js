import axiosClient from "./axiosClient";

export const getChannelMessages = async (channelId) => {
    const response = await axiosClient.get(`/channels/${channelId}/messages`);
    return response.data;
};

export const sendChannelMessage = async (channelId, content) => {
    const response = await axiosClient.post(`/channels/${channelId}/messages`, {
        content: content,
    });

    return response.data;
};

export const updateMessage = async (messageId, content) => {
    const response = await axiosClient.put(`/messages/${messageId}`, {
        content: content,
    });

    return response.data;
};

export const deleteMessage = async (messageId) => {
    const response = await axiosClient.delete(`/messages/${messageId}`);
    return response.data;
};