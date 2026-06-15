import axios from "axios";

const API_BASE_URL = "http://localhost:8080";

const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        "Content-Type": "application/json"
    }
});

const demoChannels = [
    { id: 1, name: "general", type: "PUBLIC" },
    { id: 2, name: "case-workers", type: "PRIVATE" },
    { id: 3, name: "admin-ops", type: "PRIVATE" },
    { id: 4, name: "volunteers", type: "PUBLIC" },
    { id: 5, name: "tech-support", type: "PUBLIC" }
];

const demoMessages = [
    {
        id: 1,
        senderName: "System",
        body: "Welcome to CompassChat."
    },
    {
        id: 2,
        senderName: "Prachi",
        body: "This is the main team messaging dashboard."
    },
    {
        id: 3,
        senderName: "Case Worker",
        body: "Channel messages will appear here."
    }
];

export async function getChannels() {
    try {
        const response = await api.get("/api/channels");
        return response.data;
    } catch {
        return demoChannels;
    }
}

export async function getMessages(channelId) {
    try {
        const response = await api.get(`/api/channels/${channelId}/messages`);
        return response.data;
    } catch {
        return demoMessages;
    }
}

export async function sendMessage(channelId, body) {
    try {
        const response = await api.post(`/api/channels/${channelId}/messages`, {
            body
        });
        return response.data;
    } catch {
        return {
            id: crypto.randomUUID(),
            senderName: "You",
            body
        };
    }
}