export default function DashboardPage() {
    return (
        <div>
            <h2>Dashboard</h2>
            <p className="page-subtitle">
                Welcome to CompassChat. This demo shows channels, direct messages,
                AI support, reminders, mood check-ins, and resources.
            </p>

            <div className="stats-grid">
                <div className="stat-card">
                    <h3>12</h3>
                    <p>Open Messages</p>
                </div>

                <div className="stat-card">
                    <h3>4</h3>
                    <p>Support Requests</p>
                </div>

                <div className="stat-card">
                    <h3>8</h3>
                    <p>Active Channels</p>
                </div>

                <div className="stat-card">
                    <h3>3</h3>
                    <p>Today&apos;s Reminders</p>
                </div>
            </div>

            <div className="content-grid">
                <div className="panel">
                    <h3>Recent Activity</h3>
                    <ul className="activity-list">
                        <li>New message in #housing-team</li>
                        <li>AI assistant suggested food resources</li>
                        <li>Reminder created for appointment</li>
                        <li>Support request assigned to case worker</li>
                    </ul>
                </div>

                <div className="panel">
                    <h3>System Channels</h3>
                    <div className="channel-list">
                        <span>#general</span>
                        <span>#housing-team</span>
                        <span>#wellbeing-team</span>
                        <span>#youth-services-team</span>
                    </div>
                </div>
            </div>
        </div>
    );
}