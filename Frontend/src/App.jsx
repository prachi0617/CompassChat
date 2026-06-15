import { Navigate, Route, Routes } from "react-router-dom";
import Sidebar from "./components/Sidebar.jsx";
import Header from "./components/Header.jsx";
import DashboardPage from "./pages/DashboardPage.jsx";
import ChatPage from "./pages/ChatPage.jsx";
import SupportRequestsPage from "./pages/SupportRequestsPage.jsx";

export default function App() {
    return (
        <div className="app">
            <Sidebar />

            <main className="main">
                <Header />

                <section className="page">
                    <Routes>
                        <Route path="/" element={<Navigate to="/dashboard" />} />
                        <Route path="/dashboard" element={<DashboardPage />} />
                        <Route path="/chat" element={<ChatPage />} />
                        <Route path="/support" element={<SupportRequestsPage />} />
                    </Routes>
                </section>
            </main>
        </div>
    );
}