import { Star } from 'lucide-react'
import ConversationListItem from './ConversationListItem'

export default function Sidebar({
    channels,
    dms,
    activeId,
    onSelectConversation,
    isAiActive,
    onSelectAi,
    aiUnreadCount,
}) {
    return (
        <div className="w-44 sm:w-48 border-r border-ink/8 bg-ink/[0.02] flex flex-col h-full shrink-0 py-3">
            <div className="px-3 pb-2">
                <p className="text-meta font-semibold text-ink-50 uppercase tracking-wide">Community Compass</p>
            </div>

            <div className="px-2 mb-3">
                <button
                    onClick={onSelectAi}
                    className={`w-full flex items-center gap-2 px-3 py-1.5 rounded-lg text-body text-left transition ${isAiActive ? 'bg-yellow-50 text-ink font-medium' : 'text-ink-70 hover:bg-ink/5'
                        }`}
                >
                    <Star size={15} className="text-yellow-500 shrink-0" fill="currentColor" />
                    <span className="truncate flex-1">AI Assistant</span>
                    {aiUnreadCount > 0 && (
                        <span className="min-w-[16px] h-4 px-1 rounded-full bg-yellow-500 text-ink text-[10px] font-semibold flex items-center justify-center">
                            {aiUnreadCount}
                        </span>
                    )}
                </button>
            </div>

            <div className="px-3 mb-1">
                <p className="text-meta font-semibold text-ink-50 uppercase tracking-wide">Channels</p>
            </div>
            <div className="px-2 space-y-0.5 mb-3">
                {channels.map((c) => (
                    <ConversationListItem
                        key={c.id}
                        name={c.name}
                        type="CHANNEL"
                        active={activeId === c.id}
                        onClick={() => onSelectConversation(c.id)}
                    />
                ))}
            </div>

            <div className="px-3 mb-1">
                <p className="text-meta font-semibold text-ink-50 uppercase tracking-wide">Direct messages</p>
            </div>
            <div className="px-2 space-y-0.5">
                {dms.map((d) => (
                    <ConversationListItem
                        key={d.id}
                        name={d.name}
                        type="DM"
                        active={activeId === d.id}
                        onClick={() => onSelectConversation(d.id)}
                    />
                ))}
            </div>
        </div>
    )
}
