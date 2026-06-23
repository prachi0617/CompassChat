import { useRef, useState } from 'react'
import { Bold, Italic, Link as LinkIcon, SendHorizontal, Trash2, Image as ImageIcon, X } from 'lucide-react'

const MAX_IMAGE_BYTES = 5 * 1024 * 1024 // 5MB — keep data URLs reasonable

export default function Composer({ onSend, placeholder = 'Message...', onClear, onEmptyEnter, allowImage = false }) {
    const [value, setValue] = useState('')
    const [attachment, setAttachment] = useState(null)
    const fileInputRef = useRef(null)

    const canSend = value.trim().length > 0 || !!attachment

    const submit = (e) => {
        e?.preventDefault()
        const trimmed = value.trim()
        if (!trimmed && !attachment) return
        onSend({ text: trimmed, attachment })
        setValue('')
        setAttachment(null)
    }

    const handleKeyDown = (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault()
            // In the Admin Team DM, an empty Enter (no text, no image) fires the next scripted line.
            if (!value.trim() && !attachment && onEmptyEnter) {
                onEmptyEnter()
                return
            }
            submit(e)
        }
    }

    const handlePickImage = () => fileInputRef.current?.click()

    const handleFileChange = (e) => {
        const file = e.target.files?.[0]
        e.target.value = '' // allow re-picking the same file
        if (!file) return
        if (!file.type.startsWith('image/')) return
        if (file.size > MAX_IMAGE_BYTES) {
            alert('Please choose an image under 5MB.')
            return
        }
        const reader = new FileReader()
        reader.onload = () => setAttachment({ url: reader.result, name: file.name })
        reader.readAsDataURL(file)
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
                {allowImage && (
                    <ToolbarButton title="Add image or GIF" onClick={handlePickImage}>
                        <ImageIcon size={13} />
                    </ToolbarButton>
                )}
                {onClear && (
                    <ToolbarButton title="Clear conversation" onClick={onClear}>
                        <Trash2 size={13} />
                    </ToolbarButton>
                )}
            </div>

            {allowImage && (
                <input
                    ref={fileInputRef}
                    type="file"
                    accept="image/*"
                    className="hidden"
                    onChange={handleFileChange}
                />
            )}

            {attachment && (
                <div className="mb-1.5 inline-flex items-center gap-2 rounded-lg border border-ink/10 bg-ink/5 p-1 pr-2">
                    <img
                        src={attachment.url}
                        alt={attachment.name}
                        className="h-10 w-10 rounded object-cover"
                    />
                    <span className="max-w-[140px] truncate text-meta text-ink-70">{attachment.name}</span>
                    <button
                        type="button"
                        onClick={() => setAttachment(null)}
                        aria-label="Remove image"
                        className="rounded p-0.5 text-ink-50 transition hover:bg-ink/10 hover:text-ink"
                    >
                        <X size={13} />
                    </button>
                </div>
            )}

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
                    disabled={!canSend}
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
