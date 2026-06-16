import { Routes, Route, Navigate } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import DashboardPage from "./pages/DashboardPage";
import ChatPage from "./pages/ChatPage";
import ReminderPage from "./pages/ReminderPage";
import MoodPage from "./pages/MoodPage";
import ResourcePage from "./pages/ResourcePage";
import Layout from "./components/Layout";

function App() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/login" />} />

      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />

      <Route path="/dashboard" element={<Layout><DashboardPage /></Layout>} />
      <Route path="/chat" element={<Layout><ChatPage /></Layout>} />
      <Route path="/reminders" element={<Layout><ReminderPage /></Layout>} />
      <Route path="/mood" element={<Layout><MoodPage /></Layout>} />
      <Route path="/resources" element={<Layout><ResourcePage /></Layout>} />
    </Routes>
  );
}

export default App;
