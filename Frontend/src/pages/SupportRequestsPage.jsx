import { useEffect, useState } from "react";
import { getAdminAuditLogs } from "../api/adminApi.js";

export default function SupportRequestsPage() {
    const [requests, setRequests] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        async function load() {
            try {
                // Support requests are tracked through the volunteer/integration endpoints
                const data = await getAdminAuditLogs();
                setRequests(typeof data === "string" ? [] : data || []);
            } catch {
                setRequests([]);
            } finally {
                setLoading(false);
            }
        }
        load();
    }, []);

    function markHandled(id) {
        setRequests((current) =>
            current.map((r) =>
                r.id === id ? { ...r, status: "HANDLED" } : r
            )
        );
    }

    return (
        <div>
            <h1>Support Requests</h1>
            <p className="muted">
                Reminder, mood, and resource requests from integrated projects.
            </p>

            {loading && <p className="muted">Loading support requests...</p>}

            {!loading && requests.length === 0 && (
                <p className="muted">No support requests yet.</p>
            )}

            {requests.map((req) => (
                <div key={req.id} className="card row">
                    <div>
                        <span className="badge">{req.status || "NEW"}</span>
                        <h3>{req.requestType || req.type || "Support Request"}</h3>
                        <p>{req.description || req.message || "No details"}</p>
                    </div>
                    <button className="btn" onClick={() => markHandled(req.id)}>
                        Mark Handled
                    </button>
                </div>
            ))}
        </div>
    );
}
