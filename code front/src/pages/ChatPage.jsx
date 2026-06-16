import { useState } from "react";

export default function ChatPage() {
    const [messages, setMessages] = useState([
        { sender: "Anitra", text: "Can someone review the housing request?" },
        { sender: "Prachi", text: "Yes, I will check the case notes." },
        { sender: "AI Assistant", text: "I found 2 related resources for this client." },
    ]);

    const [input, setInput] = useState("");

    const sendMessage = (e) => {
        e.preventDefault();

        if (!input.trim()) return;

        setMessages([...messages, { sender: "You", text: input }]);
        setInput("");
    };

    return (
        <div>
            <h2>Channels</h2>
            <p className="page-subtitle">Store-and-forward team messaging.</p>

            <div className="chat-layout">
                <div className="channel-panel">
                    <h3>Channels</h3>
                    <button>#general</button>
                    <button>#housing-team</button>
                    <button>#wellbeing-team</button>
                    <button>#youth-services-team</button>
                    <button>#tech-support</button>
                </div>

                <div className="chat-panel">
                    <div className="chat-header">
                        <h3>#general</h3>
                        <p>Organization-wide discussion</p>
                    </div>

                    <div className="messages">
                        {messages.map((message, index) => (
                            <div
                                key={index}
                                className={
                                    message.sender === "You"
                                        ? "message message-right"
                                        : "message message-left"
                                }
                            >
                                <strong>{message.sender}</strong>
                                <p>{message.text}</p>
                            </div>
                        ))}
                    </div>

                    <form className="message-form" onSubmit={sendMessage}>
                        <input
                            type="text"
                            placeholder="Type a message..."
                            value={input}
                            onChange={(e) => setInput(e.target.value)}
                        />
                        <button type="submit">Send</button>
                    </form>
                </div>
            </div>
        </div>
    );
}