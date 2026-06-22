import { ArrowRight, Compass } from 'lucide-react'
import { useNavigate } from 'react-router-dom'

export default function ResourceCard({ title, description, link }) {
    const navigate = useNavigate()

    return (
        <button
            onClick={() => link && navigate(link)}
            className="max-w-[88%] w-full text-left bg-white border border-mint-100 rounded-2xl px-4 py-3 hover:border-mint-300 hover:shadow-sm transition group"
        >
            <div className="flex items-center gap-2 mb-1">
                <div className="p-1.5 rounded-lg bg-mint-50 text-mint-700">
                    <Compass size={14} />
                </div>
                <p className="font-medium text-sm text-ink">{title}</p>
            </div>
            {description && <p className="text-sm text-ink-70 leading-relaxed mt-1">{description}</p>}
            {link && (
                <span className="inline-flex items-center gap-1 mt-2 text-sm font-medium text-mint-700 group-hover:gap-1.5 transition-all">
                    Learn more <ArrowRight size={12} />
                </span>
            )}
        </button>
    )
}
