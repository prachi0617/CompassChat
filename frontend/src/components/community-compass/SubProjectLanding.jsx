import { Link } from 'react-router-dom'
import { ArrowLeft, UserRound } from 'lucide-react'
import Button from '../ui/Button'
import Avatar from '../ui/Avatar'

const ACCENTS = {
    mint: { bg: 'bg-mint-50', text: 'text-mint-700' },
    pink: { bg: 'bg-pink-50', text: 'text-pink-700' },
    yellow: { bg: 'bg-yellow-50', text: 'text-ink' },
}

export default function SubProjectLanding({ name, tagline, developer, accent = 'mint' }) {
    const a = ACCENTS[accent] || ACCENTS.mint

    return (
        <div className="max-w-3xl mx-auto px-6 py-16">
            <Link
                to="/"
                className="inline-flex items-center gap-1.5 text-meta font-medium text-ink-70 hover:text-ink mb-10 transition"
            >
                <ArrowLeft size={14} /> Back to Community Compass
            </Link>

            <span className={`inline-block text-meta font-semibold px-3 py-1 rounded-full mb-4 ${a.bg} ${a.text}`}>
                Sub-project
            </span>
            <h1 className="font-display font-semibold text-3xl sm:text-4xl text-ink mb-4">{name}</h1>
            <p className="text-body text-ink-70 leading-relaxed max-w-xl mb-10">{tagline}</p>

            <div className="rounded-2xl border border-ink/8 bg-white p-6 flex items-center gap-4 max-w-md">
                <Avatar name={developer} size={48} />
                <div className="flex-1 min-w-0">
                    <p className="text-meta text-ink-50 mb-0.5">Built by</p>
                    <p className="font-display font-semibold text-ink truncate">{developer}</p>
                </div>
                <Button variant="primary" size="sm">
                    <UserRound size={14} />
                    Speak with {developer.split(' ')[0]}
                </Button>
            </div>
        </div>
    )
}
