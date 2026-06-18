import { useState } from 'react'
import { Outlet } from 'react-router-dom'
import Header from './Header'
import ChatTrigger from '../compass-chat/ChatTrigger'
import ChatPanel from '../compass-chat/ChatPanel'

export default function Layout() {
    const [chatOpen, setChatOpen] = useState(false)

    return (
        <div className="min-h-screen flex flex-col bg-[#FAFAF9]">
            <Header />

            <main className="flex-1">
                <Outlet />
            </main>

            <ChatTrigger
                isOpen={chatOpen}
                onClick={() => setChatOpen((o) => !o)}
            />

            <ChatPanel
                isOpen={chatOpen}
                onClose={() => setChatOpen(false)}
            />
        </div>
    )
}