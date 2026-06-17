import { useEffect } from 'react'
import { X } from 'lucide-react'
import Sidebar from './Sidebar'
import DemoBanner from './DemoBanner'
import MessageThread from './MessageThread'
import Composer from './Composer'
import AIConversation from './ai-assistant/AIConversation'
import { useChatStore } from '../../stores/useChatStore'
import { useAIStore } from '../../stores/useAIStore'

export default function ChatPanel({ isOpen, onClose }) {
    const {
        channels,
        dms,
        activeConversationId,
        messagesByConversation,
        loading,
        loadConversations,
        selectConversation,
        sendMessage,
    } = useChatStore()

    const aiUnreadCount = useAIStore((s) => s.unreadCount)

    // null = AI Assistant is active; any string = a channel/dm id
    const isAiActive = activeConversationId === null

    useEffect(() => {
        if (isOpen && channels.length === 0 && dms.length === 0) {
            loadConversations()
        }
    }, [isOpen, channels.length, dms.length, loadConversations])

    useEffect(() => {
        if (!isOpen) return
        const handleKey = (e) => {
            if (e.key === 'Escape') onClose()
        }
        document.addEventListener('keydown', handleKey)
        return () => document.removeEventListener('keydown', handleKey)
    }, [isOpen, onClose])

    if (!isOpen) return null

    const activeName =
        isAiActive
            ? 'AI Assistant'
            : [...channels, ...dms].find((c) => c.id === activeConversationId)?.name || 'Conversation'

    const messages = activeConversationId ? messagesByConversation[activeConversationId] : null

    return (
        <>
            <div
                className="fixed inset-0 bg-ink/20 z-40 animate-fade-in"
                onClick={onClose}
                aria-hidden="true"
            />
            <div
                role="dialog"
                aria-label="Community Compass chat"
                className="fixed top-0 right-0 h-full bg-white z-50 shadow-2xl flex flex-col animate-slide-in
                   w-full sm:w-[var(--panel-width)] max-w-full"
                style={{ width: 'min(100vw, var(--panel-width))' }}
            >
                <div className="h-14 px-4 flex items-center justify-between border-b border-ink/8 shrink-0">
                    <p className="font-display font-semibold text-ink truncate">{activeName}</p>
                    <button
                        onClick={onClose}
                        aria-label="Close chat"
                        className="p-1.5 rounded-lg text-ink-50 hover:bg-ink/5 hover:text-ink transition"
                    >
                        <X size={18} />
                    </button>
                </div>

                <DemoBanner />

                <div className="flex flex-1 overflow-hidden">
                    <Sidebar
                        channels={channels}
                        dms={dms}
                        activeId={activeConversationId}
                        onSelectConversation={selectConversation}
                        isAiActive={isAiActive}
                        onSelectAi={() => selectConversation(null)}
                        aiUnreadCount={aiUnreadCount}
                    />

                    <div className="flex-1 flex flex-col min-w-0">
                        {isAiActive ? (
                            <AIConversation />
                        ) : (
                            <>
                                <MessageThread messages={messages} loading={loading && !messages} />
                                <Composer
                                    onSend={(body) => sendMessage(activeConversationId, body)}
                                    placeholder={`Message ${activeName}`}
                                />
                            </>
                        )}
                    </div>
                </div>
            </div>
        </>
    )
}
