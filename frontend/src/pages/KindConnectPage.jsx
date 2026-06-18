import { Search, ActivitySquare, HandHeart } from 'lucide-react'
import SubProjectLanding from '../components/community-compass/SubProjectLanding'

const features = [
    {
        icon: <Search size={22} />,
        title: 'Resource Discovery',
        body: 'Find well-being services, support groups, and mental health resources tailored to your needs and location.',
    },
    {
        icon: <ActivitySquare size={22} />,
        title: 'Wellness Check-Ins',
        body: 'Regular mood and health check-ins help you track how you\'re doing and surface the right support at the right time.',
    },
    {
        icon: <HandHeart size={22} />,
        title: 'Volunteer Network',
        body: 'Connect with community volunteers and organizations ready to offer hands-on support.',
    },
]

export default function KindConnectPage() {
    return (
        <div>
            <SubProjectLanding
                name="Kind Connect"
                developer="Prachi Patel"
                accent="yellow"
                tagline="Community support and well-being resource discovery, wellness check-ins, and a volunteer network."
            />

            <div className="max-w-3xl mx-auto px-6 pb-16">
                <h2 className="font-display font-semibold text-xl text-ink mb-5">What Kind Connect offers</h2>
                <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
                    {features.map((f) => (
                        <div key={f.title} className="rounded-2xl border border-ink/8 bg-white p-5">
                            <div className="w-10 h-10 rounded-xl bg-yellow-50 text-yellow-700 flex items-center justify-center mb-3">
                                {f.icon}
                            </div>
                            <p className="font-semibold text-ink text-sm mb-1">{f.title}</p>
                            <p className="text-sm text-ink-50 leading-relaxed">{f.body}</p>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    )
}
