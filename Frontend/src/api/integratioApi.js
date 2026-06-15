import axiosClient from "./axiosClient.js";

export async function getIntegrations() {
    const response = await axiosClient.get("/integrations");
    return response.data.data;
}

export async function getIntegrationStatus() {
    const response = await axiosClient.get("/integrations/status");
    return response.data.data;
}
