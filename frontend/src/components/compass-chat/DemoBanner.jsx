import { Users } from 'lucide-react'
import { useChatStore } from '../../stores/useChatStore'

export default function DemoBanner() {
    const activeConversationId = useChatStore((s) => s.activeConversationId)
    const isAiChannel = useChatStore((s) => s.isAiChannel)
    const getAiChannelSlug = useChatStore((s) => s.getAiChannelSlug)
    const switchToSubProjectChannel = useChatStore((s) => s.switchToSubProjectChannel)
    const switchToAdminDm = useChatStore((s) => s.switchToAdminDm)

    const handleClick = () => {
        if (activeConversationId && isAiChannel(activeConversationId)) {
            const slug = getAiChannelSlug(activeConversationId)
            if (slug) {
                switchToSubProjectChannel(slug, 'User requested a live agent from the AI channel.')
                return
            }
        }
        switchToAdminDm('User requested a live agent.')
    }

    return (
        <button
            onClick={handleClick}
            className="w-full flex items-center gap-2 bg-yellow-50 text-ink px-4 py-2 text-meta border-b border-yellow-300/60 hover:bg-yellow-100 transition-colors text-left cursor-pointer"
        >
            <Users size={14} className="text-yellow-700 shrink-0" />
            <span>Click here if you would like to speak to a member of our Admin Team.</span>
        </button>
    )
}
