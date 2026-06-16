import React, { useState } from "react";
import "./HomePage.css";

function HomePage() {
    const [openFolder, setOpenFolder] = useState(null);
    const [activeChannel, setActiveChannel] = useState("compass-chat");
    const [input, setInput] = useState("");
    const [notificationCount, setNotificationCount] = useState(2);

    const [messages, setMessages] = useState([
        {
            user: "CompassChat Bot",
            time: "9:20 AM",
            text: "Welcome to #compass-chat. This is the main community support channel.",
            tag: "@channel",
        },
        {
            user: "Case Worker",
            time: "9:25 AM",
            text: "A client needs food and shelter resources today.",
            tag: "@admin",
        },
        {
            user: "Volunteer Team",
            time: "9:30 AM",
            text: "I can help with groceries or a friendly call today.",
            tag: "@volunteer",
        },
    ]);

    const sendMessage = () => {
        if (input.trim() === "") return;

        const newMessage = {
            user: "You",
            time: "Now",
            text: input,
            tag: "@chat",
        };

        setMessages([...messages, newMessage]);
        setInput("");
        setNotificationCount(notificationCount + 1);
    };

    if (openFolder === "compasschat") {
        return (
            <div className="slack-page">
                <aside className="slack-sidebar">
                    <button className="back-btn" onClick={() => setOpenFolder(null)}>
                        ← Back to Dashboard
                    </button>

                    <div className="workspace-title">
                        <h2>CompassChat</h2>
                        <p>Community Workspace</p>
                    </div>

                    <p className="sidebar-label">Channels</p>

                    {[
                        "compass-chat",
                        "community-support",
                        "food-help",
                        "shelter-help",
                        "volunteer-help",
                        "admin-alerts",
                    ].map((channel) => (
                        <button
                            key={channel}
                            className={activeChannel === channel ? "channel active" : "channel"}
                            onClick={() => setActiveChannel(channel)}
                        >
                            # {channel}
                        </button>
                    ))}

                    <p className="sidebar-label">Direct Messages</p>
                    <button className="channel">Admin Team</button>
                    <button className="channel">Volunteer Team</button>
                    <button className="channel">AI Agent</button>
                </aside>

                <main className="slack-chat">
                    <header className="chat-header">
                        <div>
                            <h1># {activeChannel}</h1>
                            <p>Community chat for support messages and notifications</p>
                        </div>

                        <div className="auth-buttons">
                            <button className="login-btn">Login</button>
                            <button className="register-btn">Register</button>
                        </div>
                    </header>

                    <div className="notification-line">
                        New chat notification: {notificationCount} unread messages
                    </div>

                    <section className="messages-area">
                        <div className="date-divider">
                            <span>Today</span>
                        </div>

                        {messages.map((message, index) => (
                            <article className="chat-message" key={index}>
                                <div className="avatar">{message.user.charAt(0)}</div>

                                <div className="message-body">
                                    <div className="message-top">
                                        <strong>{message.user}</strong>
                                        <span>{message.time}</span>
                                    </div>

                                    <p>
                                        <mark>{message.tag}</mark> {message.text}
                                    </p>

                                    <div className="reaction-row">
                                        <span>Done 1</span>
                                        <span>Like 1</span>
                                        <span>Seen 2</span>
                                    </div>
                                </div>
                            </article>
                        ))}
                    </section>

                    <div className="message-input-box">
                        <div className="format-bar">
                            <span>B</span>
                            <span>I</span>
                            <span>Link</span>
                            <span>List</span>
                            <span>Code</span>
                        </div>

                        <div className="input-row">
                            <input
                                type="text"
                                placeholder={`Message #${activeChannel}`}
                                value={input}
                                onChange={(e) => setInput(e.target.value)}
                                onKeyDown={(e) => {
                                    if (e.key === "Enter") sendMessage();
                                }}
                            />
                            <button onClick={sendMessage}>Send</button>
                        </div>
                    </div>
                </main>
            </div>
        );
    }

    if (openFolder === "resources") {
        return (
            <div className="detail-page">
                <button className="back-light-btn" onClick={() => setOpenFolder(null)}>
                    ← Back to Dashboard
                </button>

                <section className="detail-box">
                    <h1>Resources</h1>
                    <p>Quick support resources for users who need help.</p>

                    <div className="detail-grid">
                        <div className="detail-card">
                            <h3>Food</h3>
                            <p>Food banks, free meals, grocery help, and pantry support.</p>
                        </div>

                        <div className="detail-card">
                            <h3>Shelter</h3>
                            <p>Emergency shelter, housing help, and rental assistance.</p>
                        </div>

                        <div className="detail-card">
                            <h3>Healthcare</h3>
                            <p>Clinics, mental health support, and medical resources.</p>
                        </div>

                        <div className="detail-card">
                            <h3>Transportation</h3>
                            <p>Bus help, appointment rides, and transportation support.</p>
                        </div>
                    </div>
                </section>
            </div>
        );
    }

    if (openFolder === "volunteer") {
        return (
            <div className="detail-page">
                <button className="back-light-btn" onClick={() => setOpenFolder(null)}>
                    ← Back to Dashboard
                </button>

                <section className="detail-box">
                    <h1>Volunteer Support</h1>
                    <p>Connect helpers with people who need support.</p>

                    <div className="detail-grid">
                        <div className="detail-card">
                            <h3>Friendly Calls</h3>
                            <p>Volunteers can call someone who needs support or conversation.</p>
                        </div>

                        <div className="detail-card">
                            <h3>Grocery Help</h3>
                            <p>Help neighbors with grocery pickup or delivery support.</p>
                        </div>

                        <div className="detail-card">
                            <h3>Tech Help</h3>
                            <p>Help users with forms, websites, or basic technology.</p>
                        </div>

                        <div className="detail-card">
                            <h3>Ride Help</h3>
                            <p>Help connect users with transportation or appointment rides.</p>
                        </div>
                    </div>
                </section>
            </div>
        );
    }

    return (
        <div className="home-dashboard">
            <header className="home-header">
                <div>
                    <h1>CompassChat Dashboard</h1>
                    <p>Community chat, resources, volunteer support, and AI help.</p>
                </div>

                <div className="auth-buttons">
                    <button className="login-light-btn">Login</button>
                    <button className="register-btn">Register</button>
                </div>
            </header>

            <main className="home-layout">
                <section className="top-section">
                    <button
                        className="compass-folder-card"
                        onClick={() => {
                            setOpenFolder("compasschat");
                            setNotificationCount(0);
                        }}
                    >
                        {notificationCount > 0 && (
                            <div className="notification-badge">{notificationCount}</div>
                        )}

                        <div className="folder-shape">
                            <div className="folder-tab"></div>
                            <div className="folder-body">
                                <span>💬</span>
                            </div>
                        </div>

                        <h2>CompassChat</h2>
                        <p>Open the Slack-style community chat workspace.</p>

                        <div className="tag-row">
                            <span>channels</span>
                            <span>messages</span>
                            <span>notifications</span>
                        </div>
                    </button>

                    <aside className="ai-agent-box">
                        <div className="ai-top">
                            <div className="ai-icon">AI</div>
                            <div>
                                <h2>AI Agent</h2>
                                <p>Online • Ready to help</p>
                            </div>
                        </div>

                        <div className="ai-message">
                            <p>
                                I can summarize chat messages, detect urgent needs, suggest
                                resources, and notify the admin team.
                            </p>
                        </div>

                        <div className="ai-suggestion">
                            <strong>Smart Suggestion</strong>
                            <span>
                                A message says “food and shelter.” I can recommend resources and
                                alert admin.
                            </span>
                        </div>

                        <div className="ai-actions">
                            <button>Summarize Chat</button>
                            <button>Suggest Resources</button>
                        </div>

                        <input className="ai-input" placeholder="Ask AI Agent..." />
                    </aside>
                </section>

                <section className="bottom-section">
                    <button
                        className="normal-card"
                        onClick={() => setOpenFolder("resources")}
                    >
                        <div className="card-icon">R</div>
                        <h2>Resources</h2>
                        <p>Food, shelter, healthcare, transportation, and local support.</p>

                        <div className="mini-list">
                            <span>Food</span>
                            <span>Shelter</span>
                            <span>Healthcare</span>
                            <span>Rides</span>
                        </div>
                    </button>

                    <button
                        className="normal-card"
                        onClick={() => setOpenFolder("volunteer")}
                    >
                        <div className="card-icon">V</div>
                        <h2>Volunteer</h2>
                        <p>Connect helpers with people who need friendly support.</p>

                        <div className="mini-list">
                            <span>Calls</span>
                            <span>Grocery</span>
                            <span>Tech Help</span>
                            <span>Rides</span>
                        </div>
                    </button>
                </section>
            </main>
        </div>
    );
}

export default HomePage;