export default function Header() {
    return (
        <header className="header">
            <div>
                <h1>CompassChat</h1>
                <p>Messaging layer for Community Compass</p>
            </div>

            <div className="header-user">
                <span className="status-dot"></span>
                Demo User
            </div>
        </header>
    );
}