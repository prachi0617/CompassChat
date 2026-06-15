import { useState } from "react";
import ReminderCard from "../components/ReminderCard.jsx";
import ResourceCard from "../components/ResourceCard.jsx";

const demoRequests = [
    {
        id: 1,
        type: "Reminder Help",
        message: "Client needs help with an appointment reminder.",
        routedChannel: "#case-workers",
        status: "NEW"
    },
    {
        id: 2,
        type: "Well-Being Check",
        message: "Client reported feeling lonely and may need follow-up.",
        routedChannel: "#wellbeing-team",
        status: "NEW"
    },
    {
        id: 3,
        type: "Resource Help",
        message: "Client needs help understanding a housing resource.",
        routedChannel: "#housing-team",
        status: "OPEN"
    }
];

export default function SupportRequestsPage() {
    const [requests, setRequests] = useState(demoRequests);

    function markHandled(id) {
        setRequests((current) =>
            current.map((request) =>
                request.id === id ? { ...request, status: "HANDLED" } : request
            )
        );
    }

    return (
        <div>
            <h1>Support Requests</h1>
            <p className="muted">
                Reminder, mood, and resource requests appear here as CompassChat support
                requests.
            </p>

            {requests.map((request) => (
                <div key={request.id} className="card row">
                    <div>
                        <span className="badge">{request.status}</span>
                        <h3>{request.type}</h3>
                        <p>{request.message}</p>
                        <p>
                            Routed to: <strong>{request.routedChannel}</strong>
                        </p>
                    </div>

                    <button className="btn" onClick={() => markHandled(request.id)}>
                        Mark Handled
                    </button>
                </div>
            ))}

            <div className="grid">
                <ReminderCard />
                <ResourceCard />
            </div>
        </div>
    );
}