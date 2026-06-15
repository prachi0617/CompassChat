export default function AdminPage() {
    return (
        <div>
            <h1>Admin Dashboard</h1>
            <p className="muted">
                Manage CompassChat users, channels, messages, and audit records.
            </p>

            <div className="grid">
                <div className="card">
                    <span className="badge">USERS</span>
                    <h3>User Management</h3>
                    <p>Create, update, disable, and assign roles to users.</p>
                    <button className="btn">Manage Users</button>
                </div>

                <div className="card">
                    <span className="badge">CHANNELS</span>
                    <h3>Channel Management</h3>
                    <p>Create channels, archive channels, and manage memberships.</p>
                    <button className="btn">Manage Channels</button>
                </div>

                <div className="card">
                    <span className="badge">AUDIT</span>
                    <h3>Message Audit Log</h3>
                    <p>Review edited, deleted, and archived messages.</p>
                    <button className="btn">View Logs</button>
                </div>
            </div>
        </div>
    );
}