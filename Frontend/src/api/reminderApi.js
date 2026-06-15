import axiosClient from "./axiosClient.js";

export async function getReminders() {
    const response = await axiosClient.get("/reminders");
    return response.data.data;
}

export async function createReminder(data) {
    const response = await axiosClient.post("/reminders", data);
    return response.data.data;
}

export async function updateReminder(id, data) {
    const response = await axiosClient.put(`/reminders/${id}`, data);
    return response.data.data;
}

export async function deleteReminder(id) {
    const response = await axiosClient.delete(`/reminders/${id}`);
    return response.data;
}
