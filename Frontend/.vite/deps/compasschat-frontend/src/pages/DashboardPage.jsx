import { Link } from "react-router-dom";

function DashboardPage() {
  const username = localStorage.getItem("username") || "Prachi";

  return (
    <div>
      <div className="page-header">
        <h1>Welcome, {username}</h1>
        <p>Your CompassChat support dashboard</p>
      </div>

      <div className="dashboard-grid">
        <Link to="/chat" className="dashboard-card blue">
          <h2>AI Assistant</h2>
          <p>Ask for help, resources, reminders, or mood support.</p>
        </Link>

        <Link to="/reminders" className="dashboard-card purple">
          <h2>Reminders</h2>
          <p>Create reminders for medicine, appointments, bills, and self-care.</p>
        </Link>

        <Link to="/mood" className="dashboard-card green">
          <h2>Mood Check-In</h2>
          <p>Track how you feel and receive supportive suggestions.</p>
        </Link>

        <Link to="/resources" className="dashboard-card orange">
          <h2>Resources</h2>
          <p>Find food, housing, healthcare, transportation, and community help.</p>
        </Link>
      </div>
    </div>
  );
}

export default DashboardPage;
