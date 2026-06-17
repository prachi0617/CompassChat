import { Sparkles } from 'lucide-react'

export default function DemoBanner() {
    return (
        <div className="flex items-center gap-2 bg-yellow-50 text-ink px-4 py-2 text-meta border-b border-yellow-300/60">
            <Sparkles size={14} className="text-yellow-500 shrink-0" />
            <span>Demo mode — messages persist to the backend.</span>
        </div>
    )
}