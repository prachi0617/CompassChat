const VARIANTS = {
    primary: 'bg-mint-500 text-white hover:bg-mint-700',
    cta: 'bg-pink-500 text-white hover:bg-pink-700',
    ghost: 'bg-transparent text-ink hover:bg-ink/5',
    outline: 'bg-white text-ink border border-ink/15 hover:bg-ink/5',
}

const SIZES = {
    sm: 'px-3 py-1.5 text-meta',
    md: 'px-4 py-2 text-body',
    lg: 'px-5 py-2.5 text-body',
}

export default function Button({
    variant = 'primary',
    size = 'md',
    className = '',
    children,
    ...props
}) {
    return (
        <button
            className={`inline-flex items-center justify-center gap-2 rounded-full font-medium transition disabled:opacity-50 disabled:cursor-not-allowed ${VARIANTS[variant]} ${SIZES[size]} ${className}`}
            {...props}
        >
            {children}
        </button>
    )
}
