import { useState } from 'react'
import {
    Home,
    MessageCircle,
    Bell,
    FileText,
    MoreHorizontal,
    Bot,
    Send,
    Plus,
    Search,
    Hash,
    Users,
    X,
} from 'lucide-react'

const channels = [
    'homematch',
    'futurepath',
    'kindconnect',
    'firststep',
]

const directMessages = [
    'Niciah Rymer-Hillian',
    'FuturePath Contact',
    'KindConnect Contact',
    'FirstStep Contact',
]

export default function CompassChatPage() {
    const [activeChannel, setActiveChannel] = useState('ai-agent')
    const [message, setMessage] = useState('')
    const [messages, setMessages] = useState([
        {
            sender: 'AI Agent',
            text: 'Hi! I am your Community Compass AI Agent. Ask me about housing, career help, community support, or first-step resources.',
        },
    ])

    function handleSend() {
        if (!message.trim()) return

        const userMessage = {
            sender: 'You',
            text: message,
        }

        const aiMessage = {
            sender: 'AI Agent',
            text: 'Thanks for sharing. I can help you find the right project or resource. Try asking about HomeMatch, FuturePath, KindConnect, or FirstStep.',
        }

        setMessages([...messages, userMessage, aiMessage])
        setMessage('')
    }

    return (
        <div className="flex h-screen overflow-hidden bg-[#1a1d21] text-white">
            {/* Far Left Sidebar */}
            <aside className="flex w-[72px] flex-col items-center bg-[#3f0f3f] py-4">
                <div className="mb-8 flex h-10 w-10 items-center justify-center rounded-xl bg-white text-[#3f0f3f] font-bold">
                    CC
                </div>

                <SideIcon icon={<Home size={22} />} label="Home" active />
                <SideIcon icon={<MessageCircle size={22} />} label="DMs" />
                <SideIcon icon={<Bell size={22} />} label="Activity" />
                <SideIcon icon={<FileText size={22} />} label="Files" />
                <SideIcon icon={<MoreHorizontal size={22} />} label="More" />

                <div className="mt-auto flex h-10 w-10 items-center justify-center rounded-full bg-white/20">
                    +
                </div>
            </aside>

            {/* Workspace Sidebar */}
            <aside className="w-[260px] bg-[#19171d] border-r border-white/10">
                <div className="flex items-center justify-between border-b border-white/10 px-5 py-4">
                    <h1 className="text-lg font-bold">
                        Community Compass
                    </h1>

                    <button className="rounded-lg p-2 hover:bg-white/10">
                        <Plus size={18} />
                    </button>
                </div>

                <div className="px-4 py-4">
                    <button
                        type="button"
                        onClick={() => setActiveChannel('ai-agent')}
                        className={`mb-4 flex w-full items-center gap-3 rounded-lg px-3 py-2 text-left text-sm ${activeChannel === 'ai-agent'
                                ? 'bg-[#7c3aed] text-white'
                                : 'text-slate-300 hover:bg-white/10'
                            }`}
                    >
                        <Bot size={18} />
                        AI Agent
                    </button>

                    <SidebarTitle title="Channels" />

                    <div className="space-y-1">
                        {channels.map((channel) => (
                            <button
                                key={channel}
                                type="button"
                                onClick={() => setActiveChannel(channel)}
                                className={`flex w-full items-center gap-2 rounded-lg px-3 py-2 text-left text-sm ${activeChannel === channel
                                        ? 'bg-white/15 text-white'
                                        : 'text-slate-300 hover:bg-white/10'
                                    }`}
                            >
                                <Hash size={16} />
                                {channel}
                            </button>
                        ))}
                    </div>

                    <SidebarTitle title="Direct messages" />

                    <div className="space-y-1">
                        {directMessages.map((name) => (
                            <button
                                key={name}
                                type="button"
                                className="flex w-full items-center gap-2 rounded-lg px-3 py-2 text-left text-sm text-slate-300 hover:bg-white/10"
                            >
                                <span className="flex h-6 w-6 items-center justify-center rounded-md bg-mint-600 text-xs font-bold text-white">
                                    {name.charAt(0)}
                                </span>
                                <span className="truncate">{name}</span>
                            </button>
                        ))}
                    </div>
                </div>
            </aside>

            {/* Main Chat Area */}
            <main className="flex flex-1 flex-col">
                {/* Top Header */}
                <header className="flex h-[64px] items-center justify-between border-b border-white/10 bg-[#1a1d21] px-6">
                    <div className="flex items-center gap-3">
                        {activeChannel === 'ai-agent' ? (
                            <Bot size={24} className="text-mint-400" />
                        ) : (
                            <Hash size={24} className="text-slate-300" />
                        )}

                        <div>
                            <h2 className="text-lg font-bold">
                                {activeChannel === 'ai-agent'
                                    ? 'AI Agent'
                                    : activeChannel}
                            </h2>
                            <p className="text-xs text-slate-400">
                                Community Compass support channel
                            </p>
                        </div>
                    </div>

                    <div className="flex items-center gap-2 rounded-lg bg-white/10 px-3 py-2 text-sm text-slate-300">
                        <Search size={16} />
                        Search Community Compass
                    </div>
                </header>

                {/* Welcome Section */}
                <section className="flex-1 overflow-y-auto px-8 py-8">
                    <div className="mb-8 rounded-2xl border border-white/10 bg-white/5 p-6">
                        <div className="mb-4 flex h-14 w-14 items-center justify-center rounded-2xl bg-mint-600">
                            {activeChannel === 'ai-agent' ? (
                                <Bot size={30} />
                            ) : (
                                <Users size={30} />
                            )}
                        </div>

                        <h1 className="mb-2 text-3xl font-bold">
                            {activeChannel === 'ai-agent'
                                ? 'Hi, AI Agent here!'
                                : `Welcome to #${activeChannel}`}
                        </h1>

                        <p className="max-w-3xl text-slate-300">
                            {activeChannel === 'ai-agent'
                                ? 'Ask me questions about housing support, career help, community resources, or next steps.'
                                : 'This channel is for project updates, resources, and support conversations.'}
                        </p>
                    </div>

                    <div className="space-y-5">
                        {messages.map((item, index) => (
                            <div key={index} className="flex gap-3">
                                <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg bg-mint-600 font-bold">
                                    {item.sender === 'You' ? 'Y' : 'AI'}
                                </div>

                                <div>
                                    <div className="mb-1 flex items-center gap-2">
                                        <span className="font-bold">
                                            {item.sender}
                                        </span>
                                        <span className="text-xs text-slate-500">
                                            now
                                        </span>
                                    </div>

                                    <p className="text-slate-300">
                                        {item.text}
                                    </p>
                                </div>
                            </div>
                        ))}
                    </div>
                </section>

                {/* Message Box */}
                <footer className="border-t border-white/10 bg-[#1a1d21] p-5">
                    <div className="rounded-2xl border border-slate-600 bg-[#222529] p-3">
                        <textarea
                            value={message}
                            onChange={(e) => setMessage(e.target.value)}
                            placeholder={
                                activeChannel === 'ai-agent'
                                    ? 'Message AI Agent'
                                    : `Message #${activeChannel}`
                            }
                            className="h-20 w-full resize-none bg-transparent px-2 text-sm text-white outline-none placeholder:text-slate-500"
                        />

                        <div className="flex items-center justify-between border-t border-white/10 pt-3">
                            <div className="flex items-center gap-2 text-slate-400">
                                <button className="rounded-lg p-2 hover:bg-white/10">
                                    <Plus size={18} />
                                </button>
                                <button className="rounded-lg px-2 py-1 text-sm font-bold hover:bg-white/10">
                                    Aa
                                </button>
                            </div>

                            <button
                                type="button"
                                onClick={handleSend}
                                className="inline-flex items-center gap-2 rounded-lg bg-mint-600 px-4 py-2 text-sm font-semibold text-white hover:bg-mint-700"
                            >
                                <Send size={16} />
                                Send
                            </button>
                        </div>
                    </div>
                </footer>
            </main>
        </div>
    )
}

function SideIcon({ icon, label, active }) {
    return (
        <button
            type="button"
            className={`mb-5 flex flex-col items-center gap-1 text-xs ${active ? 'text-white' : 'text-slate-300'
                }`}
        >
            <div
                className={`flex h-10 w-10 items-center justify-center rounded-xl ${active ? 'bg-white/20' : 'hover:bg-white/10'
                    }`}
            >
                {icon}
            </div>
            <span>{label}</span>
        </button>
    )
}

function SidebarTitle({ title }) {
    return (
        <p className="mb-2 mt-5 px-3 text-xs font-semibold uppercase tracking-wide text-slate-500">
            {title}
        </p>
    )
}