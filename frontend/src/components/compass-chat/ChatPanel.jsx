import { useCallback, useEffect, useRef, useState } from 'react'
import { X } from 'lucide-react'
import Sidebar from './Sidebar'
import DemoBanner from './DemoBanner'
import MessageThread from './MessageThread'
import Composer from './Composer'
import AIConversation from './ai-assistant/AIConversation'
import { useChatStore } from '../../stores/useChatStore'
import { useAIStore } from '../../stores/useAIStore'
import { useAuthStore } from '../../stores/useAuthStore'

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
        getAdminDmId,
        fireNextAdminScriptLine,
        resetAdminScript,
    } = useChatStore()

    const setActiveAiChannel = useAIStore((s) => s.setActiveAiChannel)

    const socketReady = useAuthStore((s) => s.socketReady)
    const authStatus = useAuthStore((s) => s.status)

    const isAiActive = activeConversationId === null || isAiChannel(activeConversationId)

    const [hasOpened, setHasOpened] = useState(isOpen)
    const [panelWidth, setPanelWidth] = useState(420)
    const [isHovering, setIsHovering] = useState(false)
    const [dragging, setDragging] = useState(false)
    const isDragging = useRef(false)

    const onMouseMove = useCallback((e) => {
        if (!isDragging.current) return
        const newWidth = window.innerWidth - e.clientX
        setPanelWidth(Math.max(340, Math.min(860, newWidth)))
    }, [])

    const onMouseUp = useCallback(() => {
        isDragging.current = false
        setDragging(false)
        document.body.style.userSelect = ''
        window.removeEventListener('mousemove', onMouseMove)
        window.removeEventListener('mouseup', onMouseUp)
    }, [onMouseMove])

    const onResizeStart = useCallback((e) => {
        e.preventDefault()
        isDragging.current = true
        setDragging(true)
        document.body.style.userSelect = 'none'
        window.addEventListener('mousemove', onMouseMove)
        window.addEventListener('mouseup', onMouseUp)
    }, [onMouseMove, onMouseUp])

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

    // Immediately select AI assistant when panel opens
    useEffect(() => {
        if (isOpen) handleSelectConversation(null)
    }, [isOpen]) // eslint-disable-line react-hooks/exhaustive-deps

    // Load conversations once auth resolves. Channels/users come over REST
    // (token only) — no need to wait for the WebSocket handshake, which can
    // be slow or fail in dev and would otherwise leave the sidebar empty.
    useEffect(() => {
        const authResolved = authStatus === 'ready' || authStatus === 'offline'
        if (isOpen && authResolved && channels.length === 0 && dms.length === 0) {
            loadConversations()
        }
    }, [isOpen, authStatus, socketReady]) // eslint-disable-line react-hooks/exhaustive-deps

    useEffect(() => {
        if (!isOpen) return
        const handleKey = (e) => { if (e.key === 'Escape') onClose() }
        document.addEventListener('keydown', handleKey)
        return () => document.removeEventListener('keydown', handleKey)
    }, [isOpen, onClose])

    if (!hasOpened) return null

    const activeName = isAiActive
        ? activeConversationId === null
            ? 'Sage'
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
                    width: Math.min(panelWidth, window.innerWidth),
                    zIndex: 9999,
                    transform: isOpen ? 'translateX(0)' : 'translateX(100%)',
                    transition: isDragging.current ? 'none' : 'transform 250ms cubic-bezier(0.16, 1, 0.3, 1)',
                    pointerEvents: isOpen ? 'auto' : 'none',
                    position: 'fixed',
                }}
            >
                <div
                    onMouseDown={onResizeStart}
                    onMouseEnter={() => setIsHovering(true)}
                    onMouseLeave={() => setIsHovering(false)}
                    role="separator"
                    aria-orientation="vertical"
                    aria-label="Resize chat panel"
                    style={{
                        position: 'absolute', left: 0, top: 0, width: 12, height: '100%',
                        cursor: 'col-resize', zIndex: 20, touchAction: 'none',
                        display: 'flex', alignItems: 'center', justifyContent: 'center',
                        background: 'transparent',
                    }}
                >
                    {/* Always-visible grip bar — brightens on hover/drag, stays lit through the whole drag */}
                    <div
                        style={{
                            width: 4, height: 44, borderRadius: 9999,
                            background: (dragging || isHovering) ? '#3DBE8A' : 'rgba(15,23,42,0.18)',
                            transition: dragging ? 'none' : 'background 120ms ease',
                            display: 'flex', flexDirection: 'column', alignItems: 'center',
                            justifyContent: 'center', gap: 3,
                        }}
                    >
                        {[0, 1, 2].map((i) => (
                            <span
                                key={i}
                                style={{
                                    width: 2, height: 2, borderRadius: 9999,
                                    background: (dragging || isHovering) ? 'rgba(255,255,255,0.9)' : 'rgba(255,255,255,0.55)',
                                }}
                            />
                        ))}
                    </div>
                </div>
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
                                    allowImage
                                    onSend={(payload) => {
                                        const text = (payload?.text ?? '').trim()
                                        const attachment = payload?.attachment ?? null

                                        // Moods/Gifs: a typed feeling (no image) routes to the AI mood flow.
                                        if (activeConversationId === 'topic-moods-gifs' && text && !attachment) {
                                            setActiveAiChannel(null)
                                            selectConversation(null)
                                            useAIStore.getState().addUserMessage(text)
                                            return
                                        }

                                        // Everything else (including images in Moods/Gifs) posts to the channel.
                                        sendMessage(activeConversationId, text, attachment)
                                    }}
                                    placeholder={
                                        activeConversationId === 'topic-moods-gifs'
                                            ? "Share how you're feeling, or post a GIF…"
                                            : `Message ${activeName}`
                                    }
                                    onClear={
                                        activeConversationId === getAdminDmId()
                                            ? resetAdminScript
                                            : () => clearConversation(activeConversationId)
                                    }
                                    onEmptyEnter={
                                        activeConversationId === getAdminDmId()
                                            ? fireNextAdminScriptLine
                                            : undefined
                                    }
                                />
                            </>
                        )}
                    </div>
                </div>
            </div>
        </>
    )
}
