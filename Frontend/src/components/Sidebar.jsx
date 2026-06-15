import { NavLink } from "react-router-dom";

export default function Sidebar() {
    return (
        <aside className="sidebar">
            <h1>CompassChat</h1>
            <p>Secure communication workspace</p>

            <nav>
                <NavLink to="/dashboard" className={({ isActive }) => isActive ? "nav active" : "nav"}>
                    Dashboard
                </NavLink>

                <NavLink to="/chat" className={({ isActive }) => isActive ? "nav active" : "nav"}>
                    Channels
                </NavLink>

                <NavLink to="/support" className={({ isActive }) => isActive ? "nav active" : "nav"}>
                    Support Requests
                </NavLink>
            </nav>
        </aside>
    );
}