import { useEffect, useState } from "react";
import { getUsers } from "../api/userApi.js";

export default function UsersPage() {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        async function load() {
            try {
                const data = await getUsers();
                setUsers(data);
            } catch {
                setUsers([]);
            } finally {
                setLoading(false);
            }
        }
        load();
    }, []);

    return (
        <div>
            <h1>Users</h1>
            <p className="muted">All registered users and their presence status.</p>

            {loading && <p className="muted">Loading users...</p>}

            {!loading && users.length === 0 && (
                <p className="muted">No users found.</p>
            )}

            {users.map((user) => (
                <div key={user.id} className="card row">
                    <div>
                        <h3>{user.username}</h3>
                        <p>{user.email || "No email"}</p>
                        <p>
                            Role: <strong>{user.role}</strong>
                        </p>
                    </div>
                    <span className="badge">{user.presence}</span>
                </div>
            ))}
        </div>
    );
}
