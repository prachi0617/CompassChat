import { useState } from 'react'
import { Home, Compass, HeartHandshake, Newspaper } from 'lucide-react'
import ContactCardModal from '../components/community-compass/ContactCardModal'
import subProjectData from '../lib/resources.json'

const ICONS = {
    homematch: <Home size={20} />,
    futurepath: <Compass size={20} />,
    kindconnect: <HeartHandshake size={20} />,
    firststep: <Newspaper size={20} />,
}

const CONTACTS = {
    homematch: {
        name: 'Niciah Rymer-Hillian',
        initials: 'NR',
        role: 'Housing Navigation Lead',
        title: 'Project Builder',
        project: 'HomeMatch',
        email: 'niciah@example.com',
    },
    futurepath: {
        name: 'Shocka Homes',
        initials: 'SH',
        role: 'Youth Transition Support',
        title: 'Project Support Team',
        project: 'FuturePath',
        email: 'futurepath@example.com',
    },
    kindconnect: {
        name: 'Prachi Patel',
        initials: 'PP',
        role: 'Well-being Support',
        title: 'Community Care Team',
        project: 'Kind Connect',
        email: 'kindconnect@example.com',
    },
    firststep: {
        name: 'Anitra Johnson',
        initials: 'AJ',
        role: 'Community Resource Support',
        title: 'Resource Navigation Team',
        project: 'First Step',
        email: 'firststep@example.com',
    },
}

export default function Dashboard() {
    const [selectedContact, setSelectedContact] = useState(null)

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
                        onClick={() => {
                            console.log('Clicked card:', p.slug)
                            setSelectedContact(CONTACTS[p.slug])
                        }}
                    />
                ))}
            </div>

            {selectedContact && (
                <ContactCardModal
                    contact={selectedContact}
                    onClose={() => setSelectedContact(null)}
                />
            )}
        </div>
    )
}