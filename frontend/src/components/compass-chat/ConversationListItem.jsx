import { Hash, MessageSquare } from 'lucide-react'

export default function ConversationListItem({ name, active, onClick, type = 'CHANNEL' }) {
    const Icon = type === 'DM' ? MessageSquare : Hash

    return (
        <button
            onClick={onClick}
            className={`w-full flex items-center gap-2 px-3 py-1.5 rounded-lg text-body text-left transition ${active ? 'bg-mint-100 text-mint-700 font-medium' : 'text-ink-70 hover:bg-ink/5 hover:text-ink'
                }`}
        >
            <Icon size={15} className="opacity-60 shrink-0" />
            <span className="truncate">{name}</span>
        </button>
    )
}
