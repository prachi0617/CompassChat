import { Home, MessageCircle } from 'lucide-react'
import { Link } from 'react-router-dom'
import kindConnectLogo from '../assets/KindConnect_logo.jpg'
import firstStepLogo from '../assets/FirstStep_logo.png'
import futurePathLogo from '../assets/FuturePath_logo.png'

const projects = [
    {
        slug: 'homematch',
        name: 'HomeMatch',
        developer: 'Niciah Rymer-Hillian',
        tagline: 'Personalized housing navigation and an AI Housing Assistant.',
        icon: <Home size={32} />,
        accent: 'bg-orange-50 text-blue-600 border-orange-100',
    },
    {
        slug: 'futurepath',
        name: 'FuturePath',
        developer: 'Shocka Holmes',
        tagline: 'Guidance for young adults transitioning out of foster care.',
        icon: <img src={futurePathLogo} alt="FuturePath logo" className="h-11 w-11 object-contain" />,
        accent: 'bg-orange-50 text-pink-700 border-orange-100',
    },
    {
        slug: 'kindconnect',
        name: 'Kind Connect',
        developer: 'Prachi Patel',
        tagline: 'Well-being resources, check-ins, and a volunteer network.',
        icon: <img src={kindConnectLogo} alt="Kind Connect logo" className="h-11 w-11 object-contain" />,
        accent: 'bg-yellow-50 text-yellow-700 border-yellow-100',
    },
    {
        slug: 'firststep',
        name: 'First Step',
        developer: 'Anitra Johnson',
        tagline: 'Community resources, policy updates, and news for everyone.',
        icon: <img src={firstStepLogo} alt="First Step logo" className="h-11 w-11 object-contain" />,
        accent: 'bg-orange-50 text-orange-600 border-orange-100',
    },
]

export default function DashboardPage() {
    return (
        <div className="min-h-screen bg-mint-100 px-6 py-14">
            <div className="max-w-5xl mx-auto">
                {/* Hero */}
                <div className="mb-12 text-center">
                    <h1 className="font-display text-5xl font-bold text-ink mb-4">
                        Community Compass
                    </h1>
                    <p className="text-ink-70 text-xl max-w-2xl mx-auto leading-relaxed">
                        A platform connecting people to housing, youth services, wellness, and civic resources — all in one place.
                    </p>

                    {/* Yellow guide box */}
                    <div className="mt-8 inline-flex items-center gap-3 bg-yellow-50 border border-yellow-100 text-yellow-900 text-base font-medium px-6 py-3.5 rounded-2xl shadow-sm">
                        <MessageCircle size={18} className="text-yellow-700 shrink-0" />
                        Chat with Sage, our AI assistant, or a team member — click the button on the right
                    </div>
                </div>

                {/* Sub-project cards */}
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
                    {projects.map((p) => (
                        <Link
                            key={p.slug}
                            to={`/${p.slug}`}
                            className="group flex gap-5 p-6 rounded-2xl border border-white/60 bg-white hover:shadow-lg transition-shadow no-underline"
                        >
                            <div className={`w-14 h-14 rounded-xl border flex items-center justify-center shrink-0 ${p.accent}`}>
                                {p.icon}
                            </div>
                            <div className="min-w-0">
                                <p className="font-semibold text-lg text-ink group-hover:text-mint-700 transition-colors">{p.name}</p>
                                <p className="text-base text-ink-50 mt-1 leading-snug">{p.tagline}</p>
                                <p className="text-sm text-ink-50 mt-2">by {p.developer}</p>
                            </div>
                        </Link>
                    ))}
                </div>
            </div>
        </div>
    )
}
