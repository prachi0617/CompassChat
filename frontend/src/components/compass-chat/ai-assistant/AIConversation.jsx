import { useEffect, useRef, useState } from 'react'
import { useAIStore } from '../../../stores/useAIStore'
import { useChatStore } from '../../../stores/useChatStore'
import { api } from '../../../lib/api'
import Composer from '../Composer'
import SmartSuggestion from './SmartSuggestion'
import ResourceCard from './ResourceCard'
import CrisisBlock from './CrisisBlock'
import EscalateCTA from './EscalateCTA'
import { Compass } from 'lucide-react'

function ThinkingIndicator() {
    return (
        <div style={{ display: 'flex', gap: 10, padding: '8px 16px' }}>
            <div style={{ width: 30, height: 30, borderRadius: '50%', background: '#3DBE8A', color: '#fff', display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
                <Compass size={15} />
            </div>
            <div style={{ background: '#F1F5F9', borderRadius: 16, borderTopLeftRadius: 4, padding: '10px 14px', display: 'flex', alignItems: 'center', gap: 4 }}>
                <Dot delay="0s" />
                <Dot delay="0.15s" />
                <Dot delay="0.3s" />
            </div>
        </div>
    )
}

function Dot({ delay }) {
    return (
        <span
            style={{
                width: 6,
                height: 6,
                borderRadius: '50%',
                background: '#94A3B8',
                display: 'inline-block',
                animation: 'cc-bounce 1.2s infinite',
                animationDelay: delay,
            }}
        />
    )
}

function QuickActions({ onAction }) {
    const actions = [
        { label: 'Summarize chat', prompt: 'Summarize the latest messages in this channel.' },
        { label: 'Suggest resources', prompt: 'Suggest some resources that might help me right now.' },
    ]
    return (
        <div style={{ display: 'flex', gap: 8, padding: '10px 16px 4px', flexWrap: 'wrap' }}>
            {actions.map((a) => (
                <button
                    key={a.label}
                    onClick={() => onAction(a.prompt)}
                    style={{
                        border: '1px solid #E2E8F0',
                        background: '#FFFFFF',
                        color: '#334155',
                        fontSize: 12.5,
                        fontWeight: 500,
                        padding: '6px 12px',
                        borderRadius: 999,
                        cursor: 'pointer',
                    }}
                >
                    {a.label}
                </button>
            ))}
        </div>
    )
}

function AiMessageRow({ message, onEscalate }) {
    const isUser = message.from === 'user'

    if (isUser) {
        return (
            <div style={{ display: 'flex', justifyContent: 'flex-end', padding: '6px 16px' }}>
                <p
                    style={{
                        maxWidth: '80%', margin: 0, background: '#3DBE8A', color: '#fff',
                        borderRadius: 16, borderTopRightRadius: 4, padding: '8px 14px',
                        fontSize: 14, whiteSpace: 'pre-wrap', wordBreak: 'break-word',
                    }}
                >
                    {message.text}
                </p>
            </div>
        )
    }

    return (
        <div style={{ display: 'flex', gap: 10, padding: '6px 16px' }}>
            <div style={{ width: 30, height: 30, borderRadius: '50%', background: '#3DBE8A', color: '#fff', display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
                <Compass size={15} />
            </div>
            <div style={{ flex: 1, minWidth: 0 }}>
                {message.type === 'text' && (
                    <p
                        style={{
                            margin: 0, background: '#F1F5F9', color: '#0F172A',
                            borderRadius: 16, borderTopLeftRadius: 4, padding: '8px 14px',
                            fontSize: 14, maxWidth: '88%', whiteSpace: 'pre-wrap', wordBreak: 'break-word',
                        }}
                    >
                        {message.text}
                    </p>
                )}
                {message.type === 'smart-suggestion' && (
                    <SmartSuggestion title={message.title} body={message.body} actions={message.actions} />
                )}
                {message.type === 'resource-card' && (
                    <ResourceCard title={message.title} description={message.description} link={message.link} />
                )}
                {message.type === 'crisis-block' && <CrisisBlock />}
                {message.type === 'escalate-cta' && (
                    <EscalateCTA label={message.label} onClick={() => onEscalate(message.contextMessage)} />
                )}
            </div>
        </div>
    )
}

export default function AIConversation() {
    const { messages, isThinking, addUserMessage, markRead } = useAIStore()
    const switchToAdminDm = useChatStore((s) => s.switchToAdminDm)
    const bottomRef = useRef(null)
    const [hasMarkedRead, setHasMarkedRead] = useState(false)

    useEffect(() => {
        if (!hasMarkedRead) {
            markRead()
            setHasMarkedRead(true)
        }
    }, [hasMarkedRead, markRead])

    useEffect(() => {
        bottomRef.current?.scrollIntoView({ block: 'end' })
    }, [messages, isThinking])

    const handleEscalate = async (contextMessage) => {
        try { await api.aiEscalate(contextMessage) } catch { /* proceed to DM regardless */ }
        switchToAdminDm(contextMessage)
    }

    return (
        <div style={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
            <style>{`@keyframes cc-bounce { 0%, 80%, 100% { transform: translateY(0); } 40% { transform: translateY(-4px); } }`}</style>
            <QuickActions onAction={addUserMessage} />
            <div style={{ flex: 1, overflowY: 'auto', padding: '6px 0' }}>
                {messages.map((m) => (
                    <AiMessageRow key={m.id} message={m} onEscalate={handleEscalate} />
                ))}
                {isThinking && <ThinkingIndicator />}
                <div ref={bottomRef} />
            </div>
            <Composer onSend={addUserMessage} placeholder="Ask the AI Assistant..." />
        </div>
    )
}