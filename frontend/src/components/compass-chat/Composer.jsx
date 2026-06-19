import { useState } from 'react'
import { Bold, Italic, Link as LinkIcon, SendHorizontal, Trash2 } from 'lucide-react'

export default function Composer({ onSend, placeholder = 'Message...', onClear }) {
    const [value, setValue] = useState('')

    const submit = (e) => {
        e.preventDefault()
        const trimmed = value.trim()
        if (!trimmed) return
        onSend(trimmed)
        setValue('')
    }

    const handleKeyDown = (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault()
            submit(e)
        }
    }

    return (
        <form onSubmit={submit} className="border-t border-ink/8 bg-white px-3 py-2.5">
            <div className="flex items-center gap-1 mb-1.5 px-1">
                <ToolbarButton title="Bold (visual only)">
                    <Bold size={13} />
                </ToolbarButton>
                <ToolbarButton title="Italic (visual only)">
                    <Italic size={13} />
                </ToolbarButton>
                <ToolbarButton title="Link (visual only)">
                    <LinkIcon size={13} />
                </ToolbarButton>
                {onClear && (
                    <ToolbarButton title="Clear conversation" onClick={onClear}>
                        <Trash2 size={13} />
                    </ToolbarButton>
                )}
            </div>
            <div className="flex items-end gap-2 rounded-xl border border-ink/10 focus-within:border-mint-500 transition px-3 py-2 outline-none">
                <textarea
                    value={value}
                    onChange={(e) => setValue(e.target.value)}
                    onKeyDown={handleKeyDown}
                    placeholder={placeholder}
                    rows={1}
                    className="flex-1 resize-none bg-transparent outline-none text-body text-ink placeholder:text-ink-50 max-h-28"
                />
                <button
                    type="submit"
                    disabled={!value.trim()}
                    className="text-mint-500 hover:text-mint-700 disabled:text-ink/20 transition shrink-0 p-1"
                    aria-label="Send"
                >
                    <SendHorizontal size={19} />
                </button>
            </div>
        </form>
    )
}

function ToolbarButton({ children, title, onClick }) {
    return (
        <button
            type="button"
            title={title}
            tabIndex={-1}
            onClick={onClick}
            className="p-1.5 rounded text-ink-50 hover:bg-ink/5 hover:text-ink-70 transition"
        >
            {children}
        </button>
    )
}
