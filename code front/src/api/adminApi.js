import axiosClient from "./axiosClient.js";

export async function getAdminStatus() {
    const response = await axiosClient.get("/admin/status");
    return response.data.data;
}

export async function getAdminUsers() {
    const response = await axiosClient.get("/admin/users");
    return response.data.data;
}

export async function getAdminChannels() {
    const response = await axiosClient.get("/admin/channels");
    return response.data.data;
}

export async function getAdminAuditLogs() {
    const response = await axiosClient.get("/admin/audit");
    return response.data.data;
}
