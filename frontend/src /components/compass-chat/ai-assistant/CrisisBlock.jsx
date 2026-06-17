import { Phone, MessageSquareText } from 'lucide-react'

export default function CrisisBlock() {
    return (
        <div className="max-w-[88%] bg-pink-50 border border-pink-300/60 rounded-2xl px-4 py-3.5">
            <p className="font-medium text-body text-ink mb-2">You don't have to go through this alone.</p>
            <p className="text-meta text-ink-70 leading-relaxed mb-3">
                If you're in crisis or thinking about suicide, support is available right now, free and
                confidential.
            </p>
            <div className="space-y-2">
                <div className="flex items-center gap-2 text-meta text-ink">
                    <Phone size={14} className="text-pink-500 shrink-0" />
                    <span>Call or text <strong>988</strong> — Suicide &amp; Crisis Lifeline</span>
                </div>
                <div className="flex items-center gap-2 text-meta text-ink">
                    <MessageSquareText size={14} className="text-pink-500 shrink-0" />
                    <span>Text <strong>HOME</strong> to <strong>741741</strong> — Crisis Text Line</span>
                </div>
            </div>
        </div>
    )
}
