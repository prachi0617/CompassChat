import { useEffect, useState } from "react";
import { getNotificationStatus } from "../api/notificationApi.js";
import { getUnreadMentions } from "../api/mentionApi.js";

export default function NotificationsPage() {
    const [status, setStatus] = useState("");
    const [mentions, setMentions] = useState([]);

    useEffect(() => {
        getNotificationStatus()
            .then(setStatus)
            .catch(() => setStatus("Available"));

        getUnreadMentions()
            .then(setMentions)
            .catch(() => setMentions([]));
    }, []);

    return (
        <div>
            <h1>Notifications</h1>
            <p className="muted">
                Updates from channels, mentions, and admin activity. Module: {status}
            </p>

            <h2>Unread Mentions</h2>
            {mentions.length === 0 && (
                <p className="muted">No unread mentions.</p>
            )}
            {mentions.map((mention) => (
                <div key={mention.id} className="card row">
                    <div>
                        <span className="badge">MENTION</span>
                        <p>In channel: {mention.channelId}</p>
                        <small className="muted">
                            {mention.createdAt}
                        </small>
                    </div>
                </div>
            ))}
        </div>
    );
}
