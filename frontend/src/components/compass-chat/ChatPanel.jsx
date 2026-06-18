import { useEffect, useState } from 'react'
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
        clearConversation,
        isAiChannel,
    } = useChatStore()

    const setActiveAiChannel = useAIStore((s) => s.setActiveAiChannel)
    const unreadByChannel = useAIStore((s) => s.unreadByChannel)
    const aiUnreadCount = Object.values(unreadByChannel).reduce((sum, n) => sum + n, 0)

    const isAiActive = activeConversationId === null || isAiChannel(activeConversationId)

    const [hasOpened, setHasOpened] = useState(isOpen)

    useEffect(() => {
        if (isOpen) setHasOpened(true)
    }, [isOpen])

    const handleSelectConversation = (id) => {
        if (id === null || isAiChannel(id)) {
            setActiveAiChannel(id === null ? null : id)
        } else {
            setActiveAiChannel(null)
        }
        selectConversation(id)
    }

    useEffect(() => {
        if (isOpen) {
            handleSelectConversation(null)
            if (channels.length === 0 && dms.length === 0) {
                loadConversations()
            }
        }
    }, [isOpen]) // eslint-disable-line react-hooks/exhaustive-deps

    useEffect(() => {
        if (!isOpen) return
        const handleKey = (e) => { if (e.key === 'Escape') onClose() }
        document.addEventListener('keydown', handleKey)
        return () => document.removeEventListener('keydown', handleKey)
    }, [isOpen, onClose])

    if (!hasOpened) return null

    const activeName = isAiActive
        ? activeConversationId === null
            ? 'AI Assistant'
            : activeConversationId.replace('ai-', '#')
        : [...channels, ...dms].find((c) => c.id === activeConversationId)?.name || 'Conversation'

    const messages = (!isAiActive && activeConversationId)
        ? messagesByConversation[activeConversationId]
        : null

    return (
        <>

            <div
                role="dialog"
                aria-label="Community Compass chat"
                aria-hidden={!isOpen}
                onClick={(e) => e.stopPropagation()}
                className="fixed top-0 right-0 h-full bg-white shadow-2xl flex flex-col"
                style={{
                    width: 'min(100vw, var(--panel-width))',
                    zIndex: 9999,
                    transform: isOpen ? 'translateX(0)' : 'translateX(100%)',
                    transition: 'transform 250ms cubic-bezier(0.16, 1, 0.3, 1)',
                    pointerEvents: isOpen ? 'auto' : 'none',
                }}
            >
                <div className="h-14 shrink-0 border-b border-ink/8 px-4 flex items-center justify-between">
                    <p className="font-display font-semibold text-ink truncate">{activeName}</p>
                    <button
                        onClick={onClose}
                        aria-label="Close chat"
                        className="rounded-lg p-1.5 text-ink-50 transition hover:bg-ink/5 hover:text-ink"
                    >
                        <X size={18} />
                    </button>
                </div>

                <DemoBanner />

                <div className="flex flex-1 overflow-x-auto overflow-y-hidden">
                    <Sidebar
                        channels={channels}
                        dms={dms}
                        activeId={activeConversationId}
                        onSelectConversation={handleSelectConversation}
                        isAiActive={isAiActive}
                        onSelectAi={() => handleSelectConversation(null)}
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
                                    onSend={(body) => sendMessage(activeConversationId, body)}
                                    placeholder={`Message ${activeName}`}
                                    onClear={() => clearConversation(activeConversationId)}
                                />
                            </>
                        )}
                    </div>
                </div>
            </div>
        </>
    )
}
