export default function DashboardPage() {
    return (
        <div>
            <h1>Welcome to CompassChat</h1>
            <p className="muted">
                A Slack-inspired messaging platform for case workers, coordinators,
                admins, volunteers, and clients.
            </p>

            <div className="grid">
                <div className="card">
                    <span className="badge">MESSAGING</span>
                    <h3>Team Channels</h3>
                    <p>
                        Organize conversations by public channels, private channels, and
                        system channels.
                    </p>
                </div>

                <div className="card">
                    <span className="badge">CHAT</span>
                    <h3>Direct Messages</h3>
                    <p>
                        Allow users to communicate one-on-one through private direct
                        messages.
                    </p>
                </div>

                <div className="card">
                    <span className="badge">ADMIN</span>
                    <h3>Admin Controls</h3>
                    <p>
                        Manage users, channels, membership, message history, and audit
                        records.
                    </p>
                </div>
            </div>

            <div className="card">
                <span className="badge">OVERVIEW</span>
                <h3>Platform Purpose</h3>
                <p>
                    CompassChat stores messages, keeps channel history, supports role-based
                    access, and helps teams communicate in one shared workspace.
                </p>
            </div>
        </div>
    );
}