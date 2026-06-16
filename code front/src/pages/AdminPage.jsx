import { useEffect, useState } from "react";
import {
    getAdminStatus,
    getAdminUsers,
    getAdminChannels,
    getAdminAuditLogs
} from "../api/adminApi.js";

export default function AdminPage() {
    const [status, setStatus] = useState("");
    const [activeTab, setActiveTab] = useState("overview");
    const [adminData, setAdminData] = useState(null);

    useEffect(() => {
        getAdminStatus().then(setStatus).catch(() => setStatus("Unavailable"));
    }, []);

    async function handleTab(tab) {
        setActiveTab(tab);
        try {
            let data;
            switch (tab) {
                case "users":
                    data = await getAdminUsers();
                    break;
                case "channels":
                    data = await getAdminChannels();
                    break;
                case "audit":
                    data = await getAdminAuditLogs();
                    break;
                default:
                    data = null;
            }
            setAdminData(data);
        } catch {
            setAdminData("Feature placeholder");
        }
    }

    return (
        <div>
            <h1>Admin Dashboard</h1>
            <p className="muted">Status: {status}</p>

            <div className="adminTabs">
                {["overview", "users", "channels", "audit"].map((tab) => (
                    <button
                        key={tab}
                        className={`btn ${activeTab === tab ? "activeTab" : ""}`}
                        onClick={() => handleTab(tab)}
                    >
                        {tab.charAt(0).toUpperCase() + tab.slice(1)}
                    </button>
                ))}
            </div>

            {activeTab === "overview" && (
                <div className="grid">
                    <div className="card">
                        <span className="badge">USERS</span>
                        <h3>User Management</h3>
                        <p>Create, update, disable, and assign roles to users.</p>
                    </div>

                    <div className="card">
                        <span className="badge">CHANNELS</span>
                        <h3>Channel Management</h3>
                        <p>Create channels, archive channels, and manage memberships.</p>
                    </div>

                    <div className="card">
                        <span className="badge">AUDIT</span>
                        <h3>Message Audit Log</h3>
                        <p>Review edited, deleted, and archived messages.</p>
                    </div>
                </div>
            )}

            {(activeTab === "users" || activeTab === "channels" || activeTab === "audit") && (
                <div className="card">
                    <h3>{activeTab.charAt(0).toUpperCase() + activeTab.slice(1)}</h3>
                    <p>{typeof adminData === "string" ? adminData : JSON.stringify(adminData)}</p>
                </div>
            )}
        </div>
    );
}
