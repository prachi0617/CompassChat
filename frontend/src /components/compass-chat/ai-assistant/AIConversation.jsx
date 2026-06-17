import { useEffect, useRef, useState } from 'react'
import { useAIStore } from '../../../stores/useAIStore'
import { useChatStore } from '../../../stores/useChatStore'
import Composer from '../Composer'
import SmartSuggestion from './SmartSuggestion'
import ResourceCard from './ResourceCard'
import CrisisBlock from './CrisisBlock'
import EscalateCTA from './EscalateCTA'
import { Compass } from 'lucide-react'

function ThinkingIndicator() {
    return (
        <div className="flex gap-2.5 px-4 py-2">
            <div className="w-[30px] h-[30px] rounded-full bg-mint-500 text-white flex items-center justify-center shrink-0">
                <Compass size={15} />
            </div>
            <div className="bg-ink/5 rounded-2xl rounded-tl-sm px-3.5 py-2.5 flex items-center gap-1">
                <span className="w-1.5 h-1.5 rounded-full bg-ink-50 animate-bounce [animation-delay:-0.3s]" />
                <span className="w-1.5 h-1.5 rounded-full bg-ink-50 animate-bounce [animation-delay:-0.15s]" />
                <span className="w-1.5 h-1.5 rounded-full bg-ink-50 animate-bounce" />
            </div>
        </div>
    )
}

function AiMessageRow({ message, onEscalate }) {
    const isUser = message.from === 'user'

    if (isUser) {
        return (
            <div className="flex justify-end px-4 py-1.5">
                <p className="max-w-[80%] bg-mint-500 text-white rounded-2xl rounded-tr-sm px-3.5 py-2 text-body whitespace-pre-wrap break-words">
                    {message.text}
                </p>
            </div>
        )
    }

    return (
        <div className="flex gap-2.5 px-4 py-1.5">
            <div className="w-[30px] h-[30px] rounded-full bg-mint-500 text-white flex items-center justify-center shrink-0">
                <Compass size={15} />
            </div>
            <div className="flex-1 min-w-0">
                {message.type === 'text' && (
                    <p className="bg-ink/5 rounded-2xl rounded-tl-sm px-3.5 py-2 text-body text-ink max-w-[88%] whitespace-pre-wrap break-words">
                        {message.text}
                    </p>
                )}
                {message.type === 'smart-suggestion' && (
                    <SmartSuggestion title={message.title} body={message.body} />
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

    const handleEscalate = (contextMessage) => {
        switchToAdminDm(contextMessage)
    }

    return (
        <div className="flex flex-col h-full">
            <div className="flex-1 overflow-y-auto py-2">
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
