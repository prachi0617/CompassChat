import { Link } from 'react-router-dom'
import { Send } from 'lucide-react'
import './CompassChatPage.css'

const channels = [
    '# compass-chat',
    '# community-support',
    '# food-help',
    '# shelter-help',
    '# volunteer-help',
    '# admin-alerts',
]

const directMessages = ['Admin Team', 'Volunteer Team', 'AI Agent']

const messages = [
    {
        avatar: 'C',
        name: 'CompassChat Bot',
        time: '9:20 AM',
        mention: '@channel',
        text: 'Welcome to #compass-chat. This is the main community support channel.',
    },
    {
        avatar: 'C',
        name: 'Case Worker',
        time: '9:25 AM',
        mention: '@admin',
        text: 'A client needs food and shelter resources today.',
    },
    {
        avatar: 'V',
        name: 'Volunteer Team',
        time: '9:30 AM',
        mention: '@volunteer',
        text: 'I can help with groceries or a friendly call today.',
    },
]

export default function CompassChatPage() {
    return (
        <div className="compass-chat-page">
            <aside className="compass-chat-sidebar">
                <Link to="/" className="back-dashboard-btn">
                    ← Back to Dashboard
                </Link>

                <div className="workspace-block">
                    <h1>CompassChat</h1>
                    <p>Community Workspace</p>
                </div>

                <div className="sidebar-group">
                    <p className="sidebar-title">CHANNELS</p>

                    {channels.map((channel, index) => (
                        <button
                            key={channel}
                            className={index === 0 ? 'sidebar-link active' : 'sidebar-link'}
                        >
                            {channel}
                        </button>
                    ))}
                </div>

                <div className="sidebar-group">
                    <p className="sidebar-title">DIRECT MESSAGES</p>

                    {directMessages.map((dm) => (
                        <button key={dm} className="sidebar-link">
                            {dm}
                        </button>
                    ))}
                </div>
            </aside>

            <main className="compass-chat-main">
                <header className="channel-header">
                    <div>
                        <h2># compass-chat</h2>
                        <p>Community chat for support messages and notifications</p>
                    </div>

                    <div className="channel-auth">
                        <button className="channel-login">Login</button>
                        <button className="channel-register">Register</button>
                    </div>
                </header>

                <div className="channel-alert">
                    Using demo data until you log in. New chat notification: 0 unread messages
                </div>

                <div className="today-divider">
                    <span>Today</span>
                </div>

                <section className="channel-messages">
                    {messages.map((message) => (
                        <MessageItem key={message.time} message={message} />
                    ))}
                </section>

                <form className="channel-composer">
                    <div className="composer-toolbar-dark">
                        <button type="button">B</button>
                        <button type="button">I</button>
                        <button type="button">Link</button>
                        <button type="button">List</button>
                        <button type="button">Code</button>
                    </div>

                    <div className="composer-bottom">
                        <input placeholder="Message #compass-chat" />
                        <button type="submit">
                            <Send size={15} />
                            Send
                        </button>
                    </div>
                </form>
            </main>
        </div>
    )
}

function MessageItem({ message }) {
    return (
        <div className="message-item">
            <div className="message-avatar">{message.avatar}</div>

            <div className="message-body">
                <div className="message-top">
                    <strong>{message.name}</strong>
                    <span>{message.time}</span>
                </div>

                <p>
                    <span className="message-mention">{message.mention}</span>{' '}
                    {message.text}
                </p>

                <div className="message-reactions">
                    <button>Done 1</button>
                    <button>Like 1</button>
                    <button>Seen 2</button>
                </div>
            </div>
        </div>
    )
}