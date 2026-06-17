import { Home, Compass, HeartHandshake, Newspaper } from 'lucide-react'
import SubProjectCard from './SubProjectCard'
import subProjectData from '../../lib/resources.json'

const ICONS = {
    homematch: <Home size={20} />,
    futurepath: <Compass size={20} />,
    kindconnect: <HeartHandshake size={20} />,
    firststep: <Newspaper size={20} />,
}

export default function Dashboard() {
    return (
        <div className="max-w-5xl mx-auto px-6 py-16">
            <div className="max-w-2xl mb-12">
                <h1 className="font-display font-semibold text-3xl sm:text-4xl text-ink mb-3">
                    One compass, four ways forward.
                </h1>
                <p className="text-body text-ink-70 leading-relaxed">
                    Community Compass connects you to housing navigation, youth transition support,
                    well-being resources, and civic updates — all from one trusted starting point.
                </p>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-5">
                {subProjectData.subProjects.map((p) => (
                    <SubProjectCard
                        key={p.slug}
                        name={p.name}
                        tagline={p.tagline}
                        accent={p.accent}
                        icon={ICONS[p.slug]}
                        route={`/${p.slug}`}
                    />
                ))}
            </div>
        </div>
    )
}
