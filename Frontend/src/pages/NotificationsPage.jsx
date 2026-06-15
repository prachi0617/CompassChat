export default function NotificationsPage() {
    const notifications = [
        {
            id: 1,
            title: "New support request",
            message: "A housing support request was routed to #housing-team.",
            status: "Unread"
        },
        {
            id: 2,
            title: "Channel update",
            message: "A new message was posted in #case-workers.",
            status: "Unread"
        },
        {
            id: 3,
            title: "Admin alert",
            message: "A user role was updated by an admin.",
            status: "Read"
        }
    ];

    return (
        <div>
            <h1>Notifications</h1>
            <p className="muted">
                Updates from channels, support requests, and admin activity.
            </p>

            {notifications.map((notification) => (
                <div key={notification.id} className="card row">
                    <div>
                        <span className="badge">{notification.status}</span>
                        <h3>{notification.title}</h3>
                        <p>{notification.message}</p>
                    </div>

                    <button className="btn">View</button>
                </div>
            ))}
        </div>
    );
}