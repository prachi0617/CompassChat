export default function UsersPage() {
    const users = [
        {
            id: 1,
            name: "Prachi Patel",
            email: "prachi@example.com",
            role: "ADMIN",
            status: "ONLINE"
        },
        {
            id: 2,
            name: "Case Worker Sarah",
            email: "sarah@example.com",
            role: "CASE_WORKER",
            status: "AWAY"
        },
        {
            id: 3,
            name: "Client Demo",
            email: "client@example.com",
            role: "CLIENT",
            status: "OFFLINE"
        }
    ];

    return (
        <div>
            <h1>Users</h1>
            <p className="muted">Manage users, roles, and presence status.</p>

            {users.map((user) => (
                <div key={user.id} className="card row">
                    <div>
                        <h3>{user.name}</h3>
                        <p>{user.email}</p>
                        <p>
                            Role: <strong>{user.role}</strong>
                        </p>
                    </div>

                    <span className="badge">{user.status}</span>
                </div>
            ))}
        </div>
    );
}