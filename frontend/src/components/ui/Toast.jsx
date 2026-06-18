import { useEffect } from 'react'
import { AlertCircle, X } from 'lucide-react'

export default function Toast({ message, onDismiss, duration = 5000 }) {
    useEffect(() => {
        if (!duration) return
        const t = setTimeout(onDismiss, duration)
        return () => clearTimeout(t)
    }, [duration, onDismiss])

    if (!message) return null

    return (
        <div className="fixed bottom-6 left-1/2 -translate-x-1/2 z-[60] flex items-center gap-2 bg-ink text-white px-4 py-2.5 rounded-full shadow-lg animate-fade-in">
            <AlertCircle size={16} className="text-pink-300 shrink-0" />
            <span className="text-meta">{message}</span>
            <button onClick={onDismiss} className="text-white/60 hover:text-white ml-1">
                <X size={14} />
            </button>
        </div>
    )
}
