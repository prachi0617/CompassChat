import { useState } from 'react'
import { Link } from 'react-router-dom'
import { MessageCircle, Newspaper } from 'lucide-react'
import ContactCardModal from '../components/community-compass/ContactCardModal'

export default function FirstStepPage() {
    const [showContact, setShowContact] = useState(false)

    const contact = {
        name: 'Anitra Johnson',
        initials: 'AJ',
        role: 'Community Resource Support',
        title: 'Resource Navigation Team',
        project: 'First Step',
        email: 'firststep@example.com',
    }

    return (
        <div className="min-h-screen bg-stone-50 px-6 py-10 text-ink">
            <div className="mx-auto max-w-4xl">
                <Link
                    to="/"
                    className="mb-6 inline-block text-sm font-semibold text-mint-700 no-underline hover:underline"
                >
                    ← Back to Community Compass
                </Link>

                <section className="rounded-3xl border border-slate-200 bg-white p-8 shadow-sm">
                    <div className="mb-5 flex h-14 w-14 items-center justify-center rounded-2xl bg-mint-50 text-mint-700">
                        <Newspaper size={30} />
                    </div>

                    <p className="mb-2 text-xs font-bold uppercase tracking-[0.18em] text-mint-700">
                        Sub-project
                    </p>

                    <h1 className="font-display text-4xl font-semibold text-ink">
                        First Step
                    </h1>

                    <p className="mt-4 max-w-2xl text-base leading-8 text-ink-70">
                        Community resources, policy updates, and local news to help
                        people know where to start.
                    </p>

                    <button
                        type="button"
                        onClick={() => setShowContact(true)}
                        className="mt-8 inline-flex items-center gap-2 rounded-full bg-mint-500 px-5 py-3 text-sm font-bold text-white shadow-sm hover:bg-mint-700"
                    >
                        <MessageCircle size={18} />
                        Contact First Step
                    </button>
                </section>
            </div>

            {showContact && (
                <ContactCardModal
                    contact={contact}
                    onClose={() => setShowContact(false)}
                />
            )}
        </div>
    )
}
