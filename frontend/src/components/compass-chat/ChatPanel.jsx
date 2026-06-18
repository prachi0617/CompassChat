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

    const isAiActive = activeConversationId === null

    useEffect(() => {
        if (isOpen) {
            selectConversation(null)

            if (channels.length === 0 && dms.length === 0) {
                loadConversations()
            }
        }
    }, [
        isOpen,
        channels.length,
        dms.length,
        loadConversations,
        selectConversation,
    ])

    useEffect(() => {
        if (!isOpen) return

        const handleKey = (e) => {
            if (e.key === 'Escape') {
                onClose()
            }
        }

        document.addEventListener('keydown', handleKey)

        return () => {
            document.removeEventListener('keydown', handleKey)
        }
    }, [isOpen, onClose])

    if (!isOpen) return null

    const activeName = isAiActive
        ? 'AI Assistant'
        : [...channels, ...dms].find((c) => c.id === activeConversationId)?.name ||
        'Conversation'

    const messages = activeConversationId
        ? messagesByConversation[activeConversationId]
        : null

    return (
        <>
            <div
                className="fixed inset-0 z-[900] bg-ink/20 animate-fade-in"
                onClick={onClose}
                aria-hidden="true"
            />

            <div
                role="dialog"
                aria-label="Community Compass chat"
                onClick={(e) => e.stopPropagation()}
                className="fixed top-0 right-0 h-full bg-white shadow-2xl flex flex-col animate-slide-in
       w-full sm:w-[var(--panel-width)] max-w-full"
                style={{
                    width: 'min(100vw, var(--panel-width))',
                    zIndex: 9999,
                    pointerEvents: 'auto',
                }}
            >
                <div className="h-14 shrink-0 border-b border-ink/8 px-4 flex items-center justify-between">
                    <p className="font-display font-semibold text-ink truncate">
                        {activeName}
                    </p>

                    <button
                        onClick={onClose}
                        aria-label="Close chat"
                        className="rounded-lg p-1.5 text-ink-50 transition hover:bg-ink/5 hover:text-ink"
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

                    <div className="flex min-w-0 flex-1 flex-col">
                        {isAiActive ? (
                            <AIConversation />
                        ) : (
                            <>
                                <MessageThread
                                    messages={messages}
                                    loading={loading && !messages}
                                />

                                <Composer
                                    onSend={(body) =>
                                        sendMessage(activeConversationId, body)
                                    }
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