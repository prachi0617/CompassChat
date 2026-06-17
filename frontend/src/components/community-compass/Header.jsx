import { Link } from 'react-router-dom'
import logo from '../../assets/logo.svg'
import Button from '../ui/Button'

const NAV_LINKS = [
    { to: '/homematch', label: 'HomeMatch' },
    { to: '/futurepath', label: 'FuturePath' },
    { to: '/kindconnect', label: 'Kind Connect' },
    { to: '/firststep', label: 'First Step' },
]

export default function Header() {
    return (
        <header
            className="sticky top-0 z-40 bg-white/90 backdrop-blur border-b border-ink/8 flex items-center justify-between px-6"
            style={{ height: 'var(--header-height)' }}
        >
            <Link to="/" className="flex items-center gap-2.5 shrink-0">
                <img src={logo} alt="" className="w-8 h-8" />
                <span className="font-display font-semibold text-ink text-lg tracking-tight">Community Compass</span>
            </Link>

            <nav className="hidden md:flex items-center gap-1">
                {NAV_LINKS.map((link) => (
                    <Link
                        key={link.to}
                        to={link.to}
                        className="px-3 py-2 rounded-full text-body text-ink-70 hover:text-ink hover:bg-mint-50 transition"
                    >
                        {link.label}
                    </Link>
                ))}
            </nav>

            <div className="flex items-center gap-2 shrink-0">
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
