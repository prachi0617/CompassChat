import { MessageCircle } from 'lucide-react'

export default function ChatTrigger({ onClick, isOpen }) {
    return (
        <button
            onClick={onClick}
            aria-label={isOpen ? 'Close chat' : 'Open chat'}
            className="chat-trigger"
        >
            <MessageCircle size={24} strokeWidth={2} />
        </button>
    )
}