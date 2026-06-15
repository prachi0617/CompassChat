export default function DirectMessagePage() {
    const conversations = [
        {
            id: 1,
            name: "Case Worker Sarah",
            lastMessage: "I reviewed your latest message."
        },
        {
            id: 2,
            name: "Coordinator Team",
            lastMessage: "Please check the channel update."
        },
        {
            id: 3,
            name: "Volunteer Support",
            lastMessage: "I am available this afternoon."
        }
    ];

    return (
        <div>
            <h1>Direct Messages</h1>
            <p className="muted">Private one-on-one conversations.</p>

            {conversations.map((dm) => (
                <div key={dm.id} className="card row">
                    <div>
                        <h3>{dm.name}</h3>
                        <p>{dm.lastMessage}</p>
                    </div>

                    <button className="btn">Open Chat</button>
                </div>
            ))}
        </div>
    );
}