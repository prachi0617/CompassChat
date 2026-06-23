import { useState } from 'react'
import { Check, Heart, Eye } from 'lucide-react'
import Avatar from '../ui/Avatar'
import Pill from '../ui/Pill'

function formatTime(iso) {
    try {
        return new Date(iso).toLocaleTimeString(undefined, { hour: 'numeric', minute: '2-digit' })
    } catch {
        return ''
    }
}

export default function MessageBubble({ message, isOwn }) {
    const [reactions, setReactions] = useState({ done: false, liked: false })
    const senderName = message.sender?.displayName || 'Someone'

    if (message.isSystem) {
        return (
            <div className="text-center px-4 py-1.5">
                <span className="text-meta text-ink-50 italic">{message.body}</span>
            </div>
        )
    }

    return (
        <div className={`flex gap-2.5 px-4 py-2 ${isOwn ? 'flex-row-reverse' : ''}`}>
            <Avatar name={senderName} size={30} />
            <div className={`flex-1 min-w-0 ${isOwn ? 'flex flex-col items-end' : ''}`}>
                <div className={`flex items-baseline gap-2 ${isOwn ? 'flex-row-reverse' : ''}`}>
                    <span className="font-medium text-body text-ink">{senderName}</span>
                    <span className="text-meta text-ink-50">{formatTime(message.createdAt)}</span>
                </div>
                {message.body && (
                    <p
                        className={`text-body text-ink mt-0.5 max-w-[85%] whitespace-pre-wrap break-words rounded-2xl px-3.5 py-2 ${isOwn ? 'bg-mint-500 text-white rounded-tr-sm' : 'bg-ink/5 rounded-tl-sm'
                            }`}
                    >
                        {message.body}
                    </p>
                )}
                {message.attachment?.url && (
                    <img
                        src={message.attachment.url}
                        alt={message.attachment.name || 'shared image'}
                        className="mt-1 max-w-[85%] rounded-2xl border border-ink/10"
                        style={{ maxHeight: 260, objectFit: 'cover' }}
                    />
                )}
                <div className={`flex flex-wrap items-center gap-1.5 mt-1 ${isOwn ? 'flex-row-reverse' : ''}`}>
                    <Pill
                        tone={reactions.done ? 'mint' : 'neutral'}
                        icon={<Check size={11} />}
                        onClick={() => setReactions((r) => ({ ...r, done: !r.done }))}
                    >
                        Done
                    </Pill>
                    <Pill
                        tone={reactions.liked ? 'pink' : 'neutral'}
                        icon={<Heart size={11} fill={reactions.liked ? 'currentColor' : 'none'} />}
                        onClick={() => setReactions((r) => ({ ...r, liked: !r.liked }))}
                    >
                        Like
                    </Pill>
                    <Pill tone="neutral" icon={<Eye size={11} />}>
                        Seen
                    </Pill>
                </div>
            </div>
        </div>
    )
}
