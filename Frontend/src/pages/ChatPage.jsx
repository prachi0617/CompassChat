import { useEffect, useState } from "react";
import { getChannels, getMessages, sendMessage } from "../api.js";
import AttachmentUpload from "../components/AttachmentUpload.jsx";

export default function ChatPage() {
    const [channels, setChannels] = useState([]);
    const [selectedChannel, setSelectedChannel] = useState(null);
    const [messages, setMessages] = useState([]);
    const [body, setBody] = useState("");

    useEffect(() => {
        async function loadChannels() {
            const data = await getChannels();
            setChannels(data);
            setSelectedChannel(data[0]);
        }

        loadChannels();
    }, []);

    useEffect(() => {
        async function loadMessages() {
            if (!selectedChannel) return;

            const data = await getMessages(selectedChannel.id);
            setMessages(data);
        }

        loadMessages();
    }, [selectedChannel]);

    async function handleSend(event) {
        event.preventDefault();

        if (!body.trim()) return;

        const saved = await sendMessage(selectedChannel.id, body);
        setMessages((current) => [...current, saved]);
        setBody("");
    }

    return (
        <div>
            <h1>CompassChat</h1>

            <div className="chatLayout">
                <div className="channelList">
                    {channels.map((channel) => (
                        <button
                            key={channel.id}
                            onClick={() => setSelectedChannel(channel)}
                            className={
                                selectedChannel?.id === channel.id
                                    ? "channel activeChannel"
                                    : "channel"
                            }
                        >
                            #{channel.name}
                            <small>{channel.type}</small>
                        </button>
                    ))}
                </div>

                <div>
                    <h2>#{selectedChannel?.name}</h2>

                    <div className="messages">
                        {messages.map((message) => (
                            <div key={message.id} className="message">
                                <strong>{message.senderName || "User"}</strong>
                                <p>{message.body}</p>
                            </div>
                        ))}
                    </div>

                    <form onSubmit={handleSend} className="messageInput">
                        <input
                            value={body}
                            onChange={(event) => setBody(event.target.value)}
                            placeholder="Type message..."
                        />
                        <button className="btn" type="submit">
                            Send
                        </button>
                    </form>
                </div>
            </div>
        </div>
    );
}