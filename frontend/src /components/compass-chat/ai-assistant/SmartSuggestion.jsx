import { Lightbulb } from 'lucide-react'
import Button from '../../ui/Button'

export default function SmartSuggestion({ title, body, actionLabel, onAction }) {
    return (
        <div className="max-w-[88%] bg-yellow-50 border border-yellow-300/70 rounded-2xl px-4 py-3">
            <div className="flex items-center gap-2 mb-1">
                <Lightbulb size={15} className="text-yellow-500" />
                <p className="font-medium text-body text-ink">{title}</p>
            </div>
            <p className="text-meta text-ink-70 leading-relaxed">{body}</p>
            {actionLabel && (
                <Button variant="outline" size="sm" className="mt-2.5" onClick={onAction}>
                    {actionLabel}
                </Button>
            )}
        </div>
    )
}