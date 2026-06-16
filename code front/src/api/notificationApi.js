import axiosClient from "./axiosClient.js";

export async function getNotifications() {
    const response = await axiosClient.get("/notifications/status");
    return response.data;
}

export async function getNotificationStatus() {
    const response = await axiosClient.get("/notifications/status");
    return response.data.data;
}
