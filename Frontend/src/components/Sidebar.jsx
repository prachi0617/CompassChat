import { NavLink } from "react-router-dom";

export default function Sidebar() {
    return (
        <aside className="sidebar">
            <div className="brand">
                <div className="brand-icon">🧭</div>
                <div>
                    <h2>CompassChat</h2>
                    <p>Community Support</p>
                </div>
            </div>

            <nav className="nav">
                <NavLink to="/dashboard">Dashboard</NavLink>
                <NavLink to="/chat">Channels</NavLink>
                <NavLink to="/dm">Direct Messages</NavLink>
                <NavLink to="/ai">AI Assistant</NavLink>
                <NavLink to="/support">Support Requests</NavLink>
                <NavLink to="/mood">Mood Check-In</NavLink>
                <NavLink to="/reminders">Reminders</NavLink>
                <NavLink to="/resources">Resources</NavLink>
                <NavLink to="/notifications">Notifications</NavLink>
                <NavLink to="/mentions">Mentions</NavLink>
                <NavLink to="/users">Users</NavLink>
                <NavLink to="/admin">Admin</NavLink>
            </nav>
        </aside>
    );
}