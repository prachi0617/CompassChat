import { Link } from 'react-router-dom'

export default function SubProjectCard({ name, tagline, icon, route }) {
    return (
        <Link
            to={route}
            className="block rounded-3xl border border-slate-200 bg-white p-6 text-left no-underline shadow-sm transition hover:-translate-y-1 hover:shadow-md"
        >
            <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-2xl bg-mint-50 text-mint-700">
                {icon}
            </div>

            <h2 className="mb-2 text-xl font-bold text-ink">
                {name}
            </h2>

            <p className="text-sm leading-6 text-slate-600">
                {tagline}
            </p>
        </Link>
    )
}