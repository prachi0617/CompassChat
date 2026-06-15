import { useState } from "react";
import { sendChatMessage } from "../api/aiApi.js";

export default function AiChatPage() {
    const [messages, setMessages] = useState([
        {
            role: "assistant",
            content: "Hello! I'm the CompassChat AI assistant. How can I help you today?"
        }
    ]);
    const [input, setInput] = useState("");
    const [loading, setLoading] = useState(false);

    async function handleSend(event) {
        event.preventDefault();
        if (!input.trim() || loading) return;

        const userMsg = { role: "user", content: input };
        setMessages((current) => [...current, userMsg]);
        setInput("");
        setLoading(true);

        try {
            const result = await sendChatMessage(input);
            setMessages((current) => [
                ...current,
                {
                    role: "assistant",
                    content: result.response || result.message || "No response"
                }
            ]);
        } catch {
            setMessages((current) => [
                ...current,
                {
                    role: "assistant",
                    content: "Sorry, I couldn't reach the AI service. Please try again."
                }
            ]);
        } finally {
            setLoading(false);
        }
    }

    return (
        <div>
            <h1>AI Assistant</h1>
            <p className="muted">
                Ask questions, get resource recommendations, or request support.
            </p>

            <div className="aiChatContainer">
                <div className="aiMessages">
                    {messages.map((msg, i) => (
                        <div
                            key={i}
                            className={`message ${msg.role === "user" ? "userMessage" : "aiMessage"}`}
                        >
                            <strong>{msg.role === "user" ? "You" : "AI"}</strong>
                            <p>{msg.content}</p>
                        </div>
                    ))}
                    {loading && (
                        <div className="message aiMessage">
                            <strong>AI</strong>
                            <p>Thinking...</p>
                        </div>
                    )}
                </div>

                <form onSubmit={handleSend} className="messageInput">
                    <input
                        value={input}
                        onChange={(e) => setInput(e.target.value)}
                        placeholder="Ask the AI assistant..."
                        disabled={loading}
                    />
                    <button className="btn" type="submit" disabled={loading}>
                        {loading ? "Sending..." : "Send"}
                    </button>
                </form>
            </div>
        </div>
    );
}
