import { Link, useLocation, useNavigate } from "react-router-dom";
import {
  LayoutDashboard,
  MessageCircle,
  Bell,
  Smile,
  Search,
  LogOut,
} from "lucide-react";

function Sidebar() {
  const location = useLocation();
  const navigate = useNavigate();

  const links = [
    { path: "/dashboard", label: "Dashboard", icon: <LayoutDashboard size={20} /> },
    { path: "/chat", label: "AI Assistant", icon: <MessageCircle size={20} /> },
    { path: "/reminders", label: "Reminders", icon: <Bell size={20} /> },
    { path: "/mood", label: "Mood", icon: <Smile size={20} /> },
    { path: "/resources", label: "Resources", icon: <Search size={20} /> },
  ];

  const logout = () => {
    localStorage.clear();
    navigate("/login");
  };

  return (
    <aside className="sidebar">
      <div className="brand">
        <div className="brand-icon">🧭</div>
        <h2>CompassChat</h2>
        <p>Community support hub</p>
      </div>

      <nav>
        {links.map((link) => (
          <Link
            key={link.path}
            to={link.path}
            className={location.pathname === link.path ? "nav-link active" : "nav-link"}
          >
            {link.icon}
            <span>{link.label}</span>
          </Link>
        ))}
      </nav>

      <button className="logout-btn" onClick={logout}>
        <LogOut size={18} />
        Logout
      </button>
    </aside>
  );
}

export default Sidebar;
