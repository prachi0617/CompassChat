import { useEffect, useState } from "react";
import { getUsers } from "../api/userApi.js";
import { postMessage } from "../api/messageApi.js";

export default function DirectMessagePage() {
    const [users, setUsers] = useState([]);
    const [selectedUser, setSelectedUser] = useState(null);
    const [content, setContent] = useState("");
    const [messages, setMessages] = useState([]);

    useEffect(() => {
        getUsers()
            .then((data) =>
                setUsers(
                    data.filter(
                        (u) => u.id !== JSON.parse(localStorage.getItem("user") || "{}").id
                    )
                )
            )
            .catch(() => setUsers([]));
    }, []);

    async function handleSend(event) {
        event.preventDefault();
        if (!content.trim() || !selectedUser) return;

        // DM uses a dedicated channel per pair — for now send to user's DM channel
        try {
            const saved = await postMessage(selectedUser.id, content);
            setMessages((current) => [...current, saved]);
            setContent("");
        } catch {
            // silently fail
        }
    }

    return (
        <div>
            <h1>Direct Messages</h1>
            <p className="muted">Private conversations with other users.</p>

            <div className="chatLayout">
                <div className="channelList">
                    {users.map((user) => (
                        <button
                            key={user.id}
                            onClick={() => setSelectedUser(user)}
                            className={
                                selectedUser?.id === user.id
                                    ? "channel activeChannel"
                                    : "channel"
                            }
                        >
                            {user.username}
                            <small>{user.presence || "OFFLINE"}</small>
                        </button>
                    ))}
                    {users.length === 0 && (
                        <p className="muted">No other users found.</p>
                    )}
                </div>

                <div>
                    <h2>{selectedUser ? selectedUser.username : "Select a user"}</h2>

                    {selectedUser && (
                        <>
                            <div className="messages">
                                {messages.map((msg) => (
                                    <div key={msg.id} className="message">
                                        <strong>{msg.senderName || "You"}</strong>
                                        <p>{msg.content}</p>
                                    </div>
                                ))}
                                {messages.length === 0 && (
                                    <p className="muted">
                                        No messages yet. Start a conversation.
                                    </p>
                                )}
                            </div>

                            <form onSubmit={handleSend} className="messageInput">
                                <input
                                    value={content}
                                    onChange={(e) => setContent(e.target.value)}
                                    placeholder="Type message..."
                                />
                                <button className="btn" type="submit">
                                    Send
                                </button>
                            </form>
                        </>
                    )}
                </div>
            </div>
        </div>
    );
}
