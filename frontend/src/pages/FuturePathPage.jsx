import { useState } from 'react'
import { Link } from 'react-router-dom'
import { Compass, MessageCircle } from 'lucide-react'
import ContactCardModal from '../components/community-compass/ContactCardModal'

export default function FuturePathPage() {
    const [showContact, setShowContact] = useState(false)

    const contact = {
        name: 'Shocka Holmes',
        initials: 'SH',
        role: 'Youth Transition Support',
        title: 'Project Support Team',
        project: 'FuturePath',
        email: 'futurepath@example.com',
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
                    <div className="mb-5 flex h-14 w-14 items-center justify-center rounded-2xl bg-pink-50 text-pink-700">
                        <Compass size={30} />
                    </div>

                    <p className="mb-2 text-xs font-bold uppercase tracking-[0.18em] text-pink-700">
                        Sub-project
                    </p>

                    <h1 className="font-display text-4xl font-semibold text-ink">
                        FuturePath
                    </h1>

                    <p className="mt-4 max-w-2xl text-base leading-8 text-ink-70">
                        Guidance for young adults transitioning out of foster care with
                        support resources, planning tools, and next-step guidance.
                    </p>

                    <button
                        type="button"
                        onClick={() => setShowContact(true)}
                        className="mt-8 inline-flex items-center gap-2 rounded-full bg-mint-500 px-5 py-3 text-sm font-bold text-white shadow-sm hover:bg-mint-700"
                    >
                        <MessageCircle size={18} />
                        Contact FuturePath
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