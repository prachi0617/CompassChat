import axiosClient from "./axiosClient.js";

export async function sendChatMessage(message) {
    const response = await axiosClient.post("/ai/chat", { message });
    return response.data.data;
}
