import { Phone } from 'lucide-react'

export default function ServiceListingCard({ name, description, phone, website }) {
    return (
        <div className="max-w-[88%] w-full bg-white border border-slate-200 rounded-xl px-3 py-2.5 space-y-1">
            <p className="font-medium text-sm text-ink leading-snug">{name}</p>
            {description && (
                <p className="text-xs text-ink-70 leading-relaxed line-clamp-3">{description}</p>
            )}
            <div className="flex flex-wrap items-center gap-x-3 gap-y-0.5 pt-0.5">
                {phone && (
                    <span className="inline-flex items-center gap-1 text-xs text-ink-70">
                        <Phone size={11} className="shrink-0" />
                        {phone}
                    </span>
                )}
                {website && website.startsWith('http') && (
                    <a
                        href={website}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="text-xs text-mint-700 font-medium hover:underline"
                        onClick={(e) => e.stopPropagation()}
                    >
                        Website
                    </a>
                )}
            </div>
        </div>
    )
}
