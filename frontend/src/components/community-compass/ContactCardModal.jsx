import { useState } from 'react'
import { X, Mail, MessageCircle, User, Briefcase, Send } from 'lucide-react'
import './ContactCardModal.css'

export default function ContactCardModal({ contact, onClose }) {
    const [messageMode, setMessageMode] = useState(false)
    const [message, setMessage] = useState('')

    if (!contact) return null

    function handleSend() {
        if (!message.trim()) return

        alert(`Message sent to ${contact.name}: ${message}`)
        setMessage('')
        setMessageMode(false)
        onClose()
    }

    return (
        <div className="contact-modal-overlay" onClick={onClose}>
            <div
                className="contact-modal-card"
                onClick={(e) => e.stopPropagation()}
            >
                <button
                    type="button"
                    className="contact-close-btn"
                    onClick={onClose}
                    aria-label="Close contact card"
                >
                    <X size={20} />
                </button>

                <div className="contact-avatar">
                    {contact.initials}
                </div>

                <h2>{contact.name}</h2>

                <p className="contact-role">{contact.role}</p>

                <div className="contact-info-box">
                    <div>
                        <Briefcase size={18} />
                        <span>{contact.project}</span>
                    </div>

                    <div>
                        <User size={18} />
                        <span>{contact.title}</span>
                    </div>

                    <div>
                        <Mail size={18} />
                        <span>{contact.email}</span>
                    </div>
                </div>

                {!messageMode ? (
                    <>
                        <p className="contact-note">
                            Send a message to connect with the right support person for this project.
                        </p>

                        <div className="contact-actions">
                            <button
                                type="button"
                                className="cancel-btn"
                                onClick={onClose}
                            >
                                Cancel
                            </button>

                            <button
                                type="button"
                                className="message-btn"
                                onClick={() => setMessageMode(true)}
                            >
                                <MessageCircle size={17} />
                                Start Message
                            </button>
                        </div>
                    </>
                ) : (
                    <div className="message-box-area">
                        <textarea
                            value={message}
                            onChange={(e) => setMessage(e.target.value)}
                            placeholder={`Write a message to ${contact.name}...`}
                            autoFocus
                        />

                        <div className="contact-actions">
                            <button
                                type="button"
                                className="cancel-btn"
                                onClick={() => setMessageMode(false)}
                            >
                                Back
                            </button>

                            <button
                                type="button"
                                className="message-btn"
                                onClick={handleSend}
                                disabled={!message.trim()}
                            >
                                <Send size={17} />
                                Send
                            </button>
                        </div>
                    </div>
                )}
            </div>
        </div>
    )
}