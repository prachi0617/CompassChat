import { useState, useRef, useEffect } from 'react'
import { Send } from 'lucide-react'
import { api } from '../../lib/api'

const INITIAL_MESSAGES = [
    {
        id: 'seed-1',
        from: 'ai',
        type: 'text',
        text: "Hi, I'm your Community Compass guide. Tell me what's on your mind — housing, support, or just where to start — and I'll point you the right way.",
    },
]

export default function AiAssistantPanel() {
    const [messages, setMessages] = useState(INITIAL_MESSAGES)
    const [value, setValue] = useState('')
    const [sending, setSending] = useState(false)
    const [connected, setConnected] = useState(true)
    const listRef = useRef(null)

    useEffect(() => {
        listRef.current?.scrollTo({ top: listRef.current.scrollHeight })
    }, [messages, sending])

    const handleSubmit = async (e) => {
        e.preventDefault()
        const text = value.trim()
        if (!text || sending) return

        const userMsg = { id: `u-${Date.now()}`, from: 'user', type: 'text', text }
        setMessages((prev) => [...prev, userMsg])
        setValue('')
        setSending(true)

        try {
            const res = await api.askAi(text)
            const reply = res?.reply || res?.message
            setConnected(true)
            setMessages((prev) => [...prev, { id: `a-${Date.now()}`, from: 'ai', type: 'text', text: reply || "I didn't get a response back from the assistant — try again in a moment." }])
        } catch (err) {
            setConnected(false)
            setMessages((prev) => [...prev, { id: `a-${Date.now()}`, from: 'ai', type: 'text', text: "I couldn't reach the backend just now. Make sure it's running, then try again." }])
        } finally {
            setSending(false)
        }
    }

    return (
        <aside className="ai-panel">
            <h2>AI Assistant</h2>

            <div className="demo-banner" style={!connected ? { background: '#FEF2F2', color: '#B91C1C' } : undefined}>
                {connected ? 'Connected — messages are sent to the backend.' : 'Backend unreachable — check it is running.'}
            </div>

            <div className="chat-list" ref={listRef}>
                {messages.map((m) =>
                    m.from === 'user' ? <UserMessage key={m.id}>{m.text}</UserMessage> : <AssistantMessage key={m.id}>{m.text}</AssistantMessage>
                )}
                {sending && <AssistantMessage muted>Thinking…</AssistantMessage>}
            </div>

            <form className="chat-input-row" onSubmit={handleSubmit}>
                <input type="text" value={value} onChange={(e) => setValue(e.target.value)} placeholder="Ask the AI Assistant..." disabled={sending} />
                <button type="submit" disabled={!value.trim() || sending} aria-label="Send">
                    <Send size={20} />
                </button>
            </form>
        </aside>
    )
}

function AssistantMessage({ children, muted }) {
    return (
        <div className="message-row">
            <div className="avatar-dot" />
            <div className="message-bubble normal" style={muted ? { color: '#94A3B8' } : undefined}>{children}</div>
        </div>
    )
}

function UserMessage({ children }) {
    return <div className="user-message">{children}</div>
}