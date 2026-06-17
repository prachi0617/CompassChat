import { Link } from 'react-router-dom'
import { ArrowRight } from 'lucide-react'

const ACCENTS = {
    mint: { bg: 'bg-mint-50', text: 'text-mint-700', ring: 'hover:ring-mint-300', dot: 'bg-mint-500' },
    pink: { bg: 'bg-pink-50', text: 'text-pink-700', ring: 'hover:ring-pink-300', dot: 'bg-pink-500' },
    yellow: { bg: 'bg-yellow-50', text: 'text-ink', ring: 'hover:ring-yellow-300', dot: 'bg-yellow-500' },
}

export default function SubProjectCard({ name, tagline, accent = 'mint', icon, route }) {
    const a = ACCENTS[accent] || ACCENTS.mint

    return (
        <Link
            to={route}
            className={`group block rounded-2xl border border-ink/8 p-6 bg-white ring-1 ring-transparent transition hover:shadow-md ${a.ring}`}
        >
            <div className={`w-11 h-11 rounded-xl ${a.bg} ${a.text} flex items-center justify-center mb-4`}>
                {icon}
            </div>
            <h3 className="font-display font-semibold text-lg text-ink mb-1.5">{name}</h3>
            <p className="text-body text-ink-70 leading-relaxed mb-4">{tagline}</p>
            <span className={`inline-flex items-center gap-1 text-meta font-medium ${a.text} group-hover:gap-1.5 transition-all`}>
                Explore <ArrowRight size={13} />
            </span>
        </Link>
    )
}
