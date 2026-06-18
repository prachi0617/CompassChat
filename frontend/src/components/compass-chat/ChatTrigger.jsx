import { MessageCircle } from 'lucide-react'

export default function ChatTrigger({ onClick, isOpen, unreadCount = 0 }) {
    return (
        <button
            onClick={onClick}
            aria-label={isOpen ? 'Close chat' : 'Open CompassChat'}
            className="fixed right-6 top-1/2 -translate-y-1/2 z-[950] w-14 h-14 rounded-full bg-mint-500 text-white shadow-lg hover:bg-mint-700 transition-colors flex items-center justify-center"
        >
            <MessageCircle size={24} strokeWidth={2} />
            {unreadCount > 0 && (
                <span className="absolute -top-1 -right-1 min-w-[20px] h-5 px-1 rounded-full bg-yellow-500 text-ink text-[11px] font-bold flex items-center justify-center shadow">
                    {unreadCount > 9 ? '9+' : unreadCount}
                </span>
            )}
        </button>
    )
}
