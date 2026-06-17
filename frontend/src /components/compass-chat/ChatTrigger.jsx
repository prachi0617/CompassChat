import { MessageCircle } from 'lucide-react'

export default function ChatTrigger({ onClick, unreadCount = 0, isOpen }) {
    return (
        <button
            onClick={onClick}
            aria-label={isOpen ? 'Close chat' : 'Open chat'}
            className="fixed z-50 bottom-6 right-6 max-sm:right-1/2 max-sm:translate-x-1/2
                 w-14 h-14 rounded-full bg-mint-500 text-white shadow-lg shadow-mint-700/20
                 flex items-center justify-center hover:bg-mint-700 transition
                 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-mint-700"
        >
            <MessageCircle size={24} strokeWidth={2} />
            {unreadCount > 0 && !isOpen && (
                <span
                    className="absolute -top-1 -right-1 min-w-[20px] h-5 px-1 rounded-full bg-yellow-500 text-ink text-[11px] font-semibold
                     flex items-center justify-center animate-badge-pulse"
                >
                    {unreadCount > 9 ? '9+' : unreadCount}
                </span>
            )}
        </button>
    )
}
