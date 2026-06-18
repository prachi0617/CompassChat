import { Home, Compass, HeartHandshake, Newspaper, MessageCircle } from 'lucide-react'
import { Link } from 'react-router-dom'

const projects = [
    {
        slug: 'homematch',
        name: 'HomeMatch',
        developer: 'Niciah Rymer-Hillian',
        tagline: 'Personalized housing navigation and an AI Housing Assistant.',
        icon: <Home size={28} />,
        accent: 'bg-mint-50 text-mint-700 border-mint-100',
    },
    {
        slug: 'futurepath',
        name: 'FuturePath',
        developer: 'Shocka Holmes',
        tagline: 'Guidance for young adults transitioning out of foster care.',
        icon: <Compass size={28} />,
        accent: 'bg-pink-50 text-pink-700 border-pink-100',
    },
    {
        slug: 'kindconnect',
        name: 'Kind Connect',
        developer: 'Prachi Patel',
        tagline: 'Well-being resources, check-ins, and a volunteer network.',
        icon: <HeartHandshake size={28} />,
        accent: 'bg-yellow-50 text-yellow-700 border-yellow-100',
    },
    {
        slug: 'firststep',
        name: 'First Step',
        developer: 'Anitra Johnson',
        tagline: 'Community resources, policy updates, and news for everyone.',
        icon: <Newspaper size={28} />,
        accent: 'bg-mint-50 text-mint-700 border-mint-100',
    },
]

export default function DashboardPage() {
    return (
        <div className="max-w-5xl mx-auto px-6 py-12">
            {/* Hero */}
            <div className="mb-12 text-center">
                <h1 className="font-display text-4xl font-bold text-ink mb-3">
                    Community Compass
                </h1>
                <p className="text-ink-70 text-lg max-w-xl mx-auto">
                    A platform connecting people to housing, youth services, wellness, and civic resources — all in one place.
                </p>
                <div className="mt-6 inline-flex items-center gap-2 bg-mint-50 border border-mint-100 text-mint-700 text-sm font-medium px-4 py-2.5 rounded-full">
                    <MessageCircle size={15} />
                    Chat with our AI Assistant or a team member — click the button in the bottom right
                </div>
            </div>

            {/* Sub-project cards */}
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-5">
                {projects.map((p) => (
                    <Link
                        key={p.slug}
                        to={`/${p.slug}`}
                        className="group flex gap-4 p-5 rounded-2xl border border-ink/8 bg-white hover:shadow-md transition-shadow"
                    >
                        <div className={`w-12 h-12 rounded-xl border flex items-center justify-center shrink-0 ${p.accent}`}>
                            {p.icon}
                        </div>
                        <div className="min-w-0">
                            <p className="font-semibold text-ink group-hover:text-mint-700 transition-colors">{p.name}</p>
                            <p className="text-sm text-ink-50 mt-0.5">{p.tagline}</p>
                            <p className="text-xs text-ink-50 mt-2">by {p.developer}</p>
                        </div>
                    </Link>
                ))}
            </div>
        </div>
    )
}
