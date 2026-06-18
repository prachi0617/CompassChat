import { Home, Compass, HeartHandshake, Newspaper } from 'lucide-react'
import SubProjectCard from './SubProjectCard'
import subProjectData from '../../lib/resources.json'

const ICONS = {
    homematch: <Home size={22} />,
    futurepath: <Compass size={22} />,
    kindconnect: <HeartHandshake size={22} />,
    firststep: <Newspaper size={22} />,
}

export default function Dashboard() {
    return (
        <div className="max-w-5xl mx-auto px-6 py-16">
            <div className="max-w-2xl mb-12">
                <h1 className="font-display font-semibold text-3xl sm:text-4xl text-ink mb-3">
                    One compass, four ways forward
                </h1>

                <p className="text-slate-600 leading-7">
                    Choose a project to view the project builder contact card.
                </p>
            </div>

            <div className="grid gap-6 sm:grid-cols-2">
                {subProjectData.map((project) => (
                    <SubProjectCard
                        key={project.id}
                        name={project.name}
                        tagline={project.tagline}
                        route={project.route}
                        icon={ICONS[project.id]}
                    />
                ))}
            </div>
        </div>
    )
}