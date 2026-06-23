import { BookOpen, Bell, Newspaper, ExternalLink } from 'lucide-react'
import SubProjectLanding from '../components/community-compass/SubProjectLanding'

const features = [
    {
        icon: <BookOpen size={22} />,
        title: 'Community Resources',
        body: 'Access a curated directory of local services, programs, and support organizations open to everyone.',
    },
    {
        icon: <Bell size={22} />,
        title: 'Policy Updates',
        body: 'Stay informed on legislation, benefit changes, and policy developments that directly affect your community.',
    },
    {
        icon: <Newspaper size={22} />,
        title: 'Latest News',
        body: 'Civic announcements, local news, and community events — all in one place, always up to date.',
    },
]

export default function FirstStepPage() {
    return (
        <div>
            <SubProjectLanding
                name="First Step"
                developer="Anitra Johnson"
                accent="mint"
                tagline="Community resources, policy updates, and news — open to everyone."
            />

            <div className="max-w-3xl mx-auto px-6 pb-16">

                {/* Live App Link */}
                <div className="mb-8 rounded-2xl border border-mint-200 bg-mint-50 p-5 text-center">
                    <h2 className="font-display font-semibold text-xl text-ink mb-2">
                        View the Live First Step App
                    </h2>

                    <p className="text-sm text-ink-50 mb-4">
                        Open the deployed First Step demo on Vercel.
                    </p>

                    <a
                        href="https://YOUR-FIRSTSTEP-LINK.vercel.app"
                        target="_blank"
                        rel="noreferrer"
                        className="inline-flex items-center gap-2 rounded-xl bg-mint-600 px-5 py-3 text-sm font-semibold text-white no-underline shadow-sm hover:bg-mint-700 transition"
                    >
                        Open Live App
                        <ExternalLink size={16} />
                    </a>
                </div>

                <h2 className="font-display font-semibold text-xl text-ink mb-5">
                    What First Step offers
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