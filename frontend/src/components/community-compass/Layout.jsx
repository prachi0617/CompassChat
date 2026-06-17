import { useState } from 'react'
import { Outlet } from 'react-router-dom'
import Header from './Header'
import ChatTrigger from '../compass-chat/ChatTrigger'
import ChatPanel from '../compass-chat/ChatPanel'
import { useAIStore } from '../../stores/useAIStore'

export default function Layout() {
    const [chatOpen, setChatOpen] = useState(false)
    console.log("chatOpen is:", chatOpen)

    const aiUnreadCount = useAIStore((s) => s.unreadCount)

    return (
        <div className="min-h-screen flex flex-col bg-[#FAFAF9]">
            <Header />
            <main className="flex-1">
                <Outlet />
            </main>

            <ChatTrigger
                isOpen={chatOpen}
                unreadCount={aiUnreadCount}
                onClick={() => setChatOpen((o) => !o)}
            />
            <ChatPanel isOpen={chatOpen} onClose={() => setChatOpen(false)} />
        </div>
    )
}
