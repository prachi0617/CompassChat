import { ClipboardList, Route, Users, ExternalLink } from 'lucide-react'
import SubProjectLanding from '../components/community-compass/SubProjectLanding'

const features = [
    {
        icon: <ClipboardList size={22} />,
        title: 'AI-Assisted Intake',
        body: 'A fast, guided onboarding process that understands your specific situation and connects you to the right support right away.',
    },
    {
        icon: <Route size={22} />,
        title: 'Structured Guidance Plans',
        body: 'Step-by-step plans built around your goals — housing, employment, education, and independent living milestones.',
    },
    {
        icon: <Users size={22} />,
        title: 'Youth Transition Support',
        body: 'Specialized resources and case pathways designed for young adults aging out of foster care and navigating independence.',
    },
]

export default function FuturePathPage() {
    return (
        <div>
            <SubProjectLanding
                name="FuturePath"
                developer="Shocka Holmes"
                accent="pink"
                tagline="AI-assisted intake and structured guidance for young adults transitioning out of foster care."
            />

            <div className="max-w-3xl mx-auto px-6 pb-16">

                {/* Live App Link */}
                <div className="mb-8 rounded-2xl border border-pink-200 bg-pink-50 p-5 text-center">
                    <h2 className="font-display font-semibold text-xl text-ink mb-2">
                        View the Live FuturePath App
                    </h2>

                    <p className="text-sm text-ink-50 mb-4">
                        Open the deployed FuturePath Streamlit demo.
                    </p>

                    <a
                        href="https://future-path-508.streamlit.app"
                        target="_blank"
                        rel="noreferrer"
                        className="inline-flex items-center gap-2 rounded-xl bg-pink-500 px-5 py-3 text-sm font-semibold text-white no-underline shadow-sm hover:bg-pink-600 transition"
                    >
                        Open Live App
                        <ExternalLink size={16} />
                    </a>
                </div>

                <h2 className="font-display font-semibold text-xl text-ink mb-5">
                    What FuturePath offers
                </h2>

                <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
                    {features.map((f) => (
                        <div
                            key={f.title}
                            className="rounded-2xl border border-ink/8 bg-white p-5"
                        >
                            <div className="w-10 h-10 rounded-xl bg-pink-50 text-pink-700 flex items-center justify-center mb-3">
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