import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { Compass } from 'lucide-react'
import ContactCardModal from '../components/community-compass/ContactCardModal'

export default function FuturePathPage() {
    const navigate = useNavigate()
    const [showContact, setShowContact] = useState(true)

    const contact = {
        name: 'Shocka Holmes',
        initials: 'SH',
        role: 'Career Navigation Lead',
        title: 'Project Builder',
        project: 'FuturePath',
        email: 'Shockah12@gmail.com',
    }

    function handleClose() {
        setShowContact(false)
        navigate('/')
    }

    return (
        <div className="min-h-screen bg-stone-50 px-6 py-10 text-ink">
            <div className="mx-auto max-w-4xl">
                <Link
                    to="/"
                    className="mb-6 inline-flex items-center text-2xl font-bold text-mint-700 no-underline hover:text-mint-800"
                    aria-label="Back to Community Compass"
                >
                    ←
                </Link>

                <section className="rounded-3xl border border-slate-200 bg-white p-8 shadow-sm">
                    <div className="mb-5 flex h-14 w-14 items-center justify-center rounded-2xl bg-mint-50 text-mint-700">
                        <Compass size={30} />
                    </div>

                    <h1 className="text-2xl font-bold text-ink">
                        FuturePath
                    </h1>
                </section>
            </div>

            {showContact && (
                <ContactCardModal
                    contact={contact}
                    onClose={handleClose}
                />
            )}
        </div>
    )
}