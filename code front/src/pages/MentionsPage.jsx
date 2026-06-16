import { useEffect, useState } from "react";
import {
    getMyMentions,
    getUnreadMentions,
    markMentionRead
} from "../api/mentionApi.js";

export default function MentionsPage() {
    const [mentions, setMentions] = useState([]);
    const [filter, setFilter] = useState("all");
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        loadMentions();
    }, [filter]);

    async function loadMentions() {
        setLoading(true);
        try {
            const data =
                filter === "unread"
                    ? await getUnreadMentions()
                    : await getMyMentions();
            setMentions(data);
        } catch {
            setMentions([]);
        } finally {
            setLoading(false);
        }
    }

    async function handleMarkRead(id) {
        try {
            const updated = await markMentionRead(id);
            setMentions((current) =>
                current.map((m) => (m.id === id ? { ...m, read: true } : m))
            );
        } catch {
            // silently fail
        }
    }

    return (
        <div>
            <h1>Mentions</h1>
            <p className="muted">
                Messages where you've been @mentioned across channels.
            </p>

            <div className="adminTabs">
                {["all", "unread"].map((f) => (
                    <button
                        key={f}
                        className={`btn ${filter === f ? "activeTab" : ""}`}
                        onClick={() => setFilter(f)}
                    >
                        {f.charAt(0).toUpperCase() + f.slice(1)}
                    </button>
                ))}
            </div>

            {loading && <p className="muted">Loading mentions...</p>}

            {!loading && mentions.length === 0 && (
                <p className="muted">No mentions found.</p>
            )}

            {mentions.map((mention) => (
                <div key={mention.id} className="card row">
                    <div>
                        <span className="badge">
                            {mention.read ? "READ" : "UNREAD"}
                        </span>
                        <p>From user: {mention.senderUserId}</p>
                        <p>In channel: {mention.channelId}</p>
                        <small className="muted">
                            {mention.createdAt}
                        </small>
                    </div>
                    {!mention.read && (
                        <button
                            className="btn"
                            onClick={() => handleMarkRead(mention.id)}
                        >
                            Mark Read
                        </button>
                    )}
                </div>
            ))}
        </div>
    );
}
