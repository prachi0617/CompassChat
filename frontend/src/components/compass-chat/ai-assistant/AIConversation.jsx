import { useEffect, useRef, useState } from 'react'
import { useAIStore } from '../../../stores/useAIStore'
import { useChatStore } from '../../../stores/useChatStore'
import { api } from '../../../lib/api'
import Composer from '../Composer'
import SmartSuggestion from './SmartSuggestion'
import ResourceCard from './ResourceCard'
import CrisisBlock from './CrisisBlock'
import EscalateCTA from './EscalateCTA'
import HandoffCTA from './HandoffCTA'
import { Compass } from 'lucide-react'
import ServiceListingCard from './ServiceListingCard'

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
        <span style={{
            width: 6, height: 6, borderRadius: '50%', background: '#94A3B8',
            display: 'inline-block', animation: 'cc-bounce 1.2s infinite', animationDelay: delay,
        }} />
    )
}

function QuickActions({ onAction }) {
    const actions = [
        { label: 'How you are feeling', prompt: 'I want to share how I am feeling.' },
        { label: 'Help finding housing', prompt: 'I need help with housing.' },
        { label: 'Youth resources', prompt: 'I need youth transition resources.' },
        { label: 'Food', prompt: 'I need help finding food and meals.' },
    ]
    return (
        <div style={{ padding: '10px 16px 4px' }}>
            <p style={{ margin: '0 0 6px', fontSize: 11.5, color: '#94A3B8', fontWeight: 500, textTransform: 'uppercase', letterSpacing: '0.05em' }}>
                If you need help with
            </p>
            <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap', alignItems: 'flex-start' }}>
                {actions.map((a) => (
                    <button
                        key={a.label}
                        onClick={() => onAction(a.prompt)}
                        style={{
                            border: '1px solid #E2E8F0', background: '#FFFFFF', color: '#334155',
                            fontSize: 12.5, fontWeight: 500, padding: '6px 12px', borderRadius: 999,
                            cursor: 'pointer', flexShrink: 0,
                        }}
                    >
                        {a.label}
                    </button>
                ))}
            </div>
        </div>
    )
}

function AiMessageRow({ message, onEscalate, onLiveAgent }) {
    const isUser = message.from === 'user'

    if (isUser) {
        return (
            <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-end', gap: 4, padding: '6px 16px' }}>
                {message.text && (
                    <p style={{
                        maxWidth: '80%', margin: 0, background: '#3DBE8A', color: '#fff',
                        borderRadius: 16, borderTopRightRadius: 4, padding: '8px 14px',
                        fontSize: 14, whiteSpace: 'pre-wrap', wordBreak: 'break-word',
                    }}>
                        {message.text}
                    </p>
                )}
                {message.attachment?.url && (
                    <img
                        src={message.attachment.url}
                        alt={message.attachment.name || 'shared image'}
                        style={{ maxWidth: '70%', maxHeight: 220, borderRadius: 14, objectFit: 'cover' }}
                    />
                )}
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
                    <p style={{
                        margin: 0, background: '#F1F5F9', color: '#0F172A',
                        borderRadius: 16, borderTopLeftRadius: 4, padding: '8px 14px',
                        fontSize: 14, maxWidth: '88%', whiteSpace: 'pre-wrap', wordBreak: 'break-word',
                    }}>
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
                {message.type === 'handoff-cta' && (
                    <HandoffCTA
                        projectName={message.projectName}
                        projectSlug={message.projectSlug}
                        onLiveAgent={() => onLiveAgent(message.projectSlug, message.contextMessage)}
                    />
                )}
                {message.type === 'service-listing' && (
                    <ServiceListingCard
                        name={message.name}
                        description={message.description}
                        phone={message.phone}
                        website={message.website}
                    />
                )}
            </div>
        </div>
    )
}

export default function AIConversation() {
    const { isThinking, addUserMessage, markRead, clearMessages } = useAIStore()
    const activeAiChannelId = useAIStore((s) => s.activeAiChannelId)
    const messagesByChannel = useAIStore((s) => s.messagesByChannel)
    const messages = messagesByChannel[activeAiChannelId] ?? []
    const switchToSubProjectChannel = useChatStore((s) => s.switchToSubProjectChannel)
    const switchToAdminDm = useChatStore((s) => s.switchToAdminDm)
    const bottomRef = useRef(null)
    const lastUserMsgRef = useRef(null)
    const [hasMarkedRead, setHasMarkedRead] = useState(false)

    const lastUserMsgId = [...messages].reverse().find((m) => m.from === 'user')?.id

    useEffect(() => {
        if (!hasMarkedRead) {
            markRead()
            setHasMarkedRead(true)
        }
    }, [hasMarkedRead, markRead])

    useEffect(() => {
        // rAF so tall service-listing cards finish layout before we measure/scroll
        const raf = requestAnimationFrame(() => {
            if (lastUserMsgId) {
                // Pin the latest user question to the top and KEEP it pinned for the
                // whole turn (user-message render AND the later AI-batch render),
                // so the response reads from the start instead of jumping to the end.
                lastUserMsgRef.current?.scrollIntoView({ block: 'start', behavior: 'smooth' })
            } else {
                // No question asked yet (welcome message) → follow the bottom.
                bottomRef.current?.scrollIntoView({ block: 'end' })
            }
        })
        return () => cancelAnimationFrame(raf)
    }, [messages, isThinking, lastUserMsgId])

    const handleEscalate = async (contextMessage) => {
        try { await api.aiEscalate(contextMessage) } catch { /* proceed to DM regardless */ }
        switchToAdminDm(contextMessage)
    }

    const handleLiveAgent = async (slug, contextMessage) => {
        try { await api.aiEscalate(contextMessage) } catch { /* proceed regardless */ }
        switchToSubProjectChannel(slug, contextMessage)
    }

    return (
        <div style={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
            <style>{`@keyframes cc-bounce { 0%, 80%, 100% { transform: translateY(0); } 40% { transform: translateY(-4px); } }`}</style>
            <QuickActions onAction={addUserMessage} />
            <div style={{ flex: 1, overflowY: 'auto', padding: '6px 0' }}>
                {messages.map((m) => (
                    <div key={m.id} ref={m.id === lastUserMsgId ? lastUserMsgRef : undefined}>
                        <AiMessageRow
                            message={m}
                            onEscalate={handleEscalate}
                            onLiveAgent={handleLiveAgent}
                        />
                    </div>
                ))}
                {isThinking && <ThinkingIndicator />}
                <div ref={bottomRef} />
            </div>
            <Composer
                allowImage
                onSend={(p) => {
                    const payload = typeof p === 'string' ? { text: p, attachment: null } : (p ?? {})
                    addUserMessage(payload.text ?? '', payload.attachment ?? null)
                }}
                placeholder="Ask Sage..."
                onClear={clearMessages}
            />
        </div>
    )
}
