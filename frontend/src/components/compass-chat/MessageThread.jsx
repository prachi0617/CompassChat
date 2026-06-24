import { useEffect, useRef } from 'react'
import MessageBubble from './MessageBubble'

function SkeletonBubble({ align = 'left' }) {
    return (
        <div className={`flex gap-2.5 px-4 py-2 ${align === 'right' ? 'flex-row-reverse' : ''}`}>
            <div className="w-[30px] h-[30px] rounded-full bg-ink/10 animate-pulse shrink-0" />
            <div className={`flex-1 ${align === 'right' ? 'flex flex-col items-end' : ''}`}>
                <div className="h-3 w-24 bg-ink/10 rounded animate-pulse mb-1.5" />
                <div className="h-8 w-48 bg-ink/10 rounded-2xl animate-pulse" />
            </div>
        </div>
    )
}

export default function MessageThread({ conversationId, messages, loading, currentUserName = 'Guest User' }) {
    const bottomRef = useRef(null)
    const scrollRef = useRef(null)
    const openedConversationRef = useRef(null)

    useEffect(() => {
        // On first open of a conversation, start at the TOP. Only follow the
        // bottom for new messages that arrive while the same conversation is open.
        if (openedConversationRef.current !== conversationId) {
            openedConversationRef.current = conversationId
            requestAnimationFrame(() => {
                if (scrollRef.current) scrollRef.current.scrollTop = 0
            })
            return
        }
        bottomRef.current?.scrollIntoView({ block: 'end' })
    }, [messages, conversationId])

    if (loading) {
        return (
            <div className="flex-1 overflow-y-auto py-2">
                <SkeletonBubble align="left" />
                <SkeletonBubble align="right" />
                <SkeletonBubble align="left" />
            </div>
        )
    }

    if (!messages || messages.length === 0) {
        return (
            <div className="flex-1 flex items-center justify-center">
                <p className="text-body text-ink-50">No messages yet — say hi 👋</p>
            </div>
        )
    }

    return (
        <div ref={scrollRef} className="flex-1 overflow-y-auto py-2">
            {messages.map((m) => (
                <MessageBubble key={m.id} message={m} isOwn={m.sender?.displayName === currentUserName} />
            ))}
            <div ref={bottomRef} />
        </div>
    )
}
