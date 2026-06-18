import { Link } from 'react-router-dom'
import logo from '../../assets/Chat.jpg'
import Button from '../ui/Button'

const NAV_LINKS = [
    { to: '/', label: 'HomeMatch' },
    { to: '/', label: 'FuturePath' },
    { to: '/', label: 'Kind Connect' },
    { to: '/', label: 'First Step' },
]

export default function Header() {
    return (
        <header
            className="sticky top-0 z-40 flex items-center justify-between border-b border-slate-200 bg-white/90 px-6 backdrop-blur"
            style={{
                height: '64px',
                overflow: 'hidden',
            }}
        >
            <Link
                to="/"
                className="flex shrink-0 items-center gap-3 no-underline"
                style={{ height: '64px' }}
            >
                <div
                    style={{
                        width: '44px',
                        height: '44px',
                        minWidth: '44px',
                        maxWidth: '44px',
                        overflow: 'hidden',
                        borderRadius: '50%',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                    }}
                >
                    <img
                        src={logo}
                        alt="Community Compass logo"
                        style={{
                            width: '120px',
                            height: '90px',

                            display: 'block',
                        }}
                    />
                </div>

                <span className="font-display text-lg font-semibold tracking-tight text-ink">
                    Community Compass
                </span>
            </Link>

            <nav className="hidden items-center gap-1 md:flex">
                {NAV_LINKS.map((link) => (
                    <Link
                        key={link.to}
                        to={link.to}
                        className="rounded-full px-3 py-2 text-body text-ink-70 no-underline transition hover:bg-mint-50 hover:text-ink"
                    >
                        {link.label}
                    </Link>
                ))}
            </nav>

            <div className="flex shrink-0 items-center gap-2">
                <Button variant="ghost" size="sm" title="Coming soon">
                    Log in
                </Button>

                <Button variant="primary" size="sm" title="Coming soon">
                    Register
                </Button>
            </div>
        </header>
    )
}