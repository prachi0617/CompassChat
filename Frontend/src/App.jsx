import { Navigate, Route, Routes } from "react-router-dom";

import Sidebar from "./components/Sidebar.jsx";
import Header from "./components/Header.jsx";

import DashboardPage from "./pages/DashboardPage.jsx";
import ChatPage from "./pages/ChatPage.jsx";
import DirectMessagePage from "./pages/DirectMessagePage.jsx";
import AdminPage from "./pages/AdminPage.jsx";
import UsersPage from "./pages/UsersPage.jsx";
import MoodPage from "./pages/MoodPage.jsx";
import ReminderPage from "./pages/ReminderPage.jsx";
import ResourcePage from "./pages/ResourcePage.jsx";
import NotificationsPage from "./pages/NotificationsPage.jsx";
import MentionsPage from "./pages/MentionsPage.jsx";
import SupportRequestsPage from "./pages/SupportRequestsPage.jsx";
import AiChatPage from "./pages/AiChatPage.jsx";

function MainLayout() {
    return (
        <div className="app">
            <Sidebar />

            <main className="main">
                <Header />

                <section className="page">
                    <Routes>
                        <Route path="/" element={<Navigate to="/dashboard" replace />} />
                        <Route path="/dashboard" element={<DashboardPage />} />
                        <Route path="/chat" element={<ChatPage />} />
                        <Route path="/dm" element={<DirectMessagePage />} />
                        <Route path="/admin" element={<AdminPage />} />
                        <Route path="/users" element={<UsersPage />} />
                        <Route path="/mood" element={<MoodPage />} />
                        <Route path="/reminders" element={<ReminderPage />} />
                        <Route path="/resources" element={<ResourcePage />} />
                        <Route path="/notifications" element={<NotificationsPage />} />
                        <Route path="/mentions" element={<MentionsPage />} />
                        <Route path="/support" element={<SupportRequestsPage />} />
                        <Route path="/ai" element={<AiChatPage />} />
                        <Route path="*" element={<Navigate to="/dashboard" replace />} />
                    </Routes>
                </section>
            </main>
        </div>
    );
}

export default function App() {
    return <MainLayout />;
}