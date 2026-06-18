import { UserRound } from 'lucide-react'
import Button from '../../ui/Button'

export default function EscalateCTA({ label = 'Talk to a human', onClick }) {
    return (
        <Button variant="cta" size="md" onClick={onClick} className="max-w-[88%]">
            <UserRound size={16} />
            {label}
        </Button>
    )
}
