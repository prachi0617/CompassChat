const TONES = {
    neutral: 'bg-ink/5 text-ink-70',
    mint: 'bg-mint-50 text-mint-700',
    pink: 'bg-pink-50 text-pink-700',
    yellow: 'bg-yellow-50 text-yellow-500',
}

export default function Pill({ tone = 'neutral', icon, onClick, children, className = '' }) {
    const Tag = onClick ? 'button' : 'span'
    return (
        <Tag
            onClick={onClick}
            className={`inline-flex items-center gap-1 rounded-full px-2.5 py-1 text-meta font-medium ${TONES[tone]} ${onClick ? 'hover:opacity-80 transition' : ''} ${className}`}
        >
            {icon}
            {children}
        </Tag>
    )
}
