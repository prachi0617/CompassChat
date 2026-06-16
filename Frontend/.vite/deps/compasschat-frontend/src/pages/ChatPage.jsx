import { useState } from "react";
import { sendAiMessage } from "../api/aiApi";

function ChatPage() {
  const [messages, setMessages] = useState([
    {
      sender: "assistant",
      text: "Hi, I am your AI assistant. How can I help you today?",
    },
  ]);

  const [input, setInput] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSend = async (e) => {
    e.preventDefault();

    if (!input.trim()) return;

    const userMessage = input;

    setMessages((prev) => [
      ...prev,
      {
        sender: "user",
        text: userMessage,
      },
    ]);

    setInput("");
    setLoading(true);

    try {
      const userId = localStorage.getItem("userId") || 1;

      const data = await sendAiMessage(userMessage, userId);

      const reply =
        data.response ||
        data.message ||
        data.answer ||
        "I understand. I can help you with reminders, mood support, or resources.";

      setMessages((prev) => [
        ...prev,
        {
          sender: "assistant",
          text: reply,
        },
      ]);
    } catch (err) {
      setMessages((prev) => [
        ...prev,
        {
          sender: "assistant",
          text: "I could not connect to the AI right now, but I can still help you find resources or create reminders.",
        },
      ]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <div className="page-header">
        <h1>AI Assistant</h1>
        <p>Ask for community resources, reminders, mood support, or next steps.</p>
      </div>

      <div className="chat-box">
        <div className="chat-messages">
          {messages.map((msg, index) => (
            <div
              key={index}
              className={msg.sender === "user" ? "message-row right" : "message-row left"}
            >
              <div className={msg.sender === "user" ? "message user" : "message assistant"}>
                {msg.text}
              </div>
            </div>
          ))}

          {loading && (
            <div className="message-row left">
              <div className="message assistant">Thinking...</div>
            </div>
          )}
        </div>

        <form className="chat-input" onSubmit={handleSend}>
          <input
            type="text"
            placeholder="Type your message..."
            value={input}
            onChange={(e) => setInput(e.target.value)}
          />

          <button type="submit">Send</button>
        </form>
      </div>
    </div>
  );
}

export default ChatPage;
