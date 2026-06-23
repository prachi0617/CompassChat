import { Map, Sliders, Bot, ExternalLink } from 'lucide-react'
import SubProjectLanding from '../components/community-compass/SubProjectLanding'

const features = [
    {
        icon: <Sliders size={22} />,
        title: 'Eligibility-Based Matching',
        body: 'Answer a few questions and get matched to housing options you actually qualify for — no guesswork.',
    },
    {
        icon: <Map size={22} />,
        title: 'Interactive Maps',
        body: 'Browse available units, shelters, and transitional housing on an interactive map with real-time availability.',
    },
    {
        icon: <Bot size={22} />,
        title: 'AI Housing Assistant',
        body: 'Ask questions about vouchers, applications, tenant rights, or next steps — Sage answers instantly.',
    },
]

export default function HomeMatchPage() {
    return (
        <div>
            <SubProjectLanding
                name="HomeMatch"
                developer="Niciah Rymer-Hillian"
                accent="mint"
                tagline="A personalized housing navigation tool offering eligibility-based matching, interactive maps, and an AI Housing Assistant."
            />

            <div className="max-w-3xl mx-auto px-6 pb-16">

                {/* Live App Link */}
                <div className="mb-8 rounded-2xl border border-mint-200 bg-mint-50 p-5 text-center">
                    <h2 className="font-display font-semibold text-xl text-ink mb-2">
                        View the Live HomeMatch App
                    </h2>

                    <p className="text-sm text-ink-50 mb-4">
                        Open the deployed HomeMatch demo.
                    </p>

                    <a
                        href="https://homematch-web-delta.vercel.app/"
                        target="_blank"
                        rel="noreferrer"
                        className="inline-flex items-center gap-2 rounded-xl bg-mint-600 px-5 py-3 text-sm font-semibold text-white no-underline shadow-sm hover:bg-mint-700 transition"
                    >
                        Open Live App
                        <ExternalLink size={16} />
                    </a>
                </div>

                <h2 className="font-display font-semibold text-xl text-ink mb-5">
                    What HomeMatch offers
                </h2>

                <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
                    {features.map((f) => (
                        <div
                            key={f.title}
                            className="rounded-2xl border border-ink/8 bg-white p-5"
                        >
                            <div className="w-10 h-10 rounded-xl bg-mint-50 text-mint-700 flex items-center justify-center mb-3">
                                {f.icon}
                            </div>

                            <p className="font-semibold text-ink text-sm mb-1">
                                {f.title}
                            </p>

                            <p className="text-sm text-ink-50 leading-relaxed">
                                {f.body}
                            </p>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    )
}