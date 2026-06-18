import { useState, useEffect } from 'react'
import { Outlet, useSearchParams, useNavigate } from 'react-router-dom'
import Header from './Header'
import ChatTrigger from '../compass-chat/ChatTrigger'
import ChatPanel from '../compass-chat/ChatPanel'
import { useChatStore } from '../../stores/useChatStore'
import { useAIStore } from '../../stores/useAIStore'

const SLUG_TO_AI_CHANNEL = {
    'homematch-help': 'ai-homematch-help',
    'futurepath-help': 'ai-futurepath-help',
    'kindconnect-help': 'ai-kindconnect-help',
    'firststep-help': 'ai-firststep-help',
}

export default function Layout() {
    const [chatOpen, setChatOpen] = useState(() => {
        if (!localStorage.getItem('cc_chat_welcomed')) {
            localStorage.setItem('cc_chat_welcomed', '1')
            return true
        }
        return false
    })

    const [searchParams] = useSearchParams()
    const navigate = useNavigate()
    const selectConversation = useChatStore((s) => s.selectConversation)
    const isAiChannel = useChatStore((s) => s.isAiChannel)
    const setActiveAiChannel = useAIStore((s) => s.setActiveAiChannel)
    const aiUnreadCount = useAIStore((s) => Object.values(s.unreadByChannel).reduce((sum, n) => sum + n, 0))

    useEffect(() => {
        const chatParam = searchParams.get('chat')
        const channelParam = searchParams.get('channel')

        if (chatParam === 'open') {
            setChatOpen(true)

            if (channelParam) {
                const aiChannelId = SLUG_TO_AI_CHANNEL[channelParam]
                if (aiChannelId) {
                    setActiveAiChannel(aiChannelId)
                    selectConversation(aiChannelId)
                }
            }

            // Clear the query params without adding to history
            navigate(window.location.pathname, { replace: true })
        }
    }, [searchParams]) // eslint-disable-line react-hooks/exhaustive-deps

    return (
        <div className="min-h-screen flex flex-col bg-[#FAFAF9]">
            <Header />

            <main className="flex-1">
                <Outlet />
            </main>

            <ChatTrigger
                isOpen={chatOpen}
                onClick={() => setChatOpen((o) => !o)}
                unreadCount={aiUnreadCount}
            />

            <ChatPanel
                isOpen={chatOpen}
                onClose={() => setChatOpen(false)}
            />
        </div>
    )
}
