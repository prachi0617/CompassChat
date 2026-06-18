import {
    Home,
    Compass,
    HeartHandshake,
    Newspaper,
    Send,
} from 'lucide-react'

import './DashboardPage.css'
import { useState } from 'React'

const projects = [
    {
        slug: 'homematch',
        name: 'HomeMatch',
        tagline: 'Personalized housing navigation and an AI Housing Assistant.',
        icon: <Home size={36} />,
        color: 'mint',
    },
    {
        slug: 'futurepath',
        name: 'FuturePath',
        tagline: 'Guidance for young adults transitioning out of foster care.',
        icon: <Compass size={36} />,
        color: 'pink',
    },
    {
        slug: 'kindconnect',
        name: 'Kind Connect',
        tagline: 'Well-being resources, check-ins, and a volunteer network.',
        icon: <HeartHandshake size={36} />,
        color: 'yellow',
    },
    {
        slug: 'firststep',
        name: 'First Step',
        tagline: 'Community resources, policy updates, and news for everyone.',
        icon: <Newspaper size={36} />,
        color: 'mint',
    },
]

export default function DashboardPage() {
    return (
        <div className="dashboard-page">
            <header className="dashboard-header">
                <nav className="main-nav">
                    <a href="/homematch">HomeMatch</a>
                    <a href="/futurepath">FuturePath</a>
                    <a href="/kindconnect">Kind Connect</a>
                    <a href="/firststep">First Step</a>
                </nav>
            </header>

            <main className="dashboard-main">
                {/* LEFT SIDE: BIG AI ASSISTANT */}
                <section className="left-section">
                    <div className="hero">
                        <h1>CompassChat</h1>

                        <p>
                            AI Assistant for Community Compass.
                        </p>
                    </div>

                    <aside className="ai-panel">
                        <h2>Community - Chat</h2>

                        <div className="chat-list">



                        </div>

                        <div className="chat-input-row">
                            <span>Ask the AI Assistant...</span>

                            <button>
                                <Send size={24} />
                            </button>
                        </div>
                    </aside>
                </section>

                {/* RIGHT SIDE: 4 SMALL CARDS */}
                <section className="project-grid">
                    {projects.map((project) => (
                        <a
                            key={project.slug}
                            href={`/${project.slug}`}
                            className="project-card"
                        >
                            <div className={`project-icon ${project.color}`}>
                                {project.icon}
                            </div>

                            <div>
                                <h2>{project.name}</h2>
                                <p>{project.tagline}</p>
                            </div>
                        </a>
                    ))}
                </section>
            </main>
        </div>
    )
}

function AssistantMessage({ children }) {
    return (
        <div className="message-row">
            <div className="avatar-dot" />
            <div className="message-bubble normal">{children}</div>
        </div>
    )
}

function AssistantCard({ title, body }) {
    return (
        <div className="message-row">
            <div className="avatar-dot" />
            <div className="message-bubble crisis">
                <strong>{title}</strong>
                <span>{body}</span>
            </div>
        </div>
    )
}

function ResourceCard({ title, body }) {
    return (
        <div className="message-row">
            <div className="avatar-dot" />
            <div className="message-bubble resource">
                <strong>{title}</strong>
                <span>{body}</span>
            </div>
        </div>
    )
}

function UserMessage({ children }) {
    return <div className="user-message">{children}</div>
}