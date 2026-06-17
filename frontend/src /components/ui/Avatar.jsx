function initials(name = '') {
    const parts = name.trim().split(/\s+/)
    if (parts.length === 1) return parts[0].slice(0, 2).toUpperCase()
    return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase()
}

function colorFor(name = '') {
    const palette = ['#3DBE8A', '#EC4899', '#F59E0B', '#475569']
    let hash = 0
    for (let i = 0; i < name.length; i++) hash = (hash * 31 + name.charCodeAt(i)) % palette.length
    return palette[Math.abs(hash) % palette.length]
}

export default function Avatar({ name, size = 32 }) {
    return (
        <div
            className="rounded-full flex items-center justify-center text-white font-medium select-none shrink-0"
            style={{ width: size, height: size, backgroundColor: colorFor(name || ''), fontSize: size * 0.38 }}
        >
            {initials(name)}
        </div>
    )
}
