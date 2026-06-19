import { ArrowRight, HeadphonesIcon } from 'lucide-react'
import { useNavigate } from 'react-router-dom'

export default function HandoffCTA({ projectName, projectSlug, onLiveAgent }) {
    const navigate = useNavigate()
    return (
        <div style={{
            border: '1px solid #E2E8F0', borderRadius: 12, padding: '12px 14px',
            background: '#FAFAF9', maxWidth: '88%', display: 'flex', flexDirection: 'column', gap: 8,
        }}>
            <p style={{ margin: 0, fontSize: 13.5, color: '#334155' }}>
                Want me to connect you with someone in <strong>{projectName}</strong>?
            </p>
            <div style={{ display: 'flex', flexDirection: 'column', gap: 6 }}>
                <button
                    onClick={() => navigate(`/${projectSlug}`)}
                    style={{
                        width: '100%', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 6,
                        background: '#3DBE8A', color: '#fff', border: 'none', borderRadius: 8,
                        padding: '8px 12px', fontSize: 13, fontWeight: 500, cursor: 'pointer',
                    }}
                >
                    <ArrowRight size={13} /> Take me there
                </button>
                <button
                    onClick={onLiveAgent}
                    style={{
                        width: '100%', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 6,
                        background: '#fff', color: '#334155', border: '1px solid #E2E8F0', borderRadius: 8,
                        padding: '8px 12px', fontSize: 13, fontWeight: 500, cursor: 'pointer',
                    }}
                >
                    <HeadphonesIcon size={13} /> Get live support
                </button>
            </div>
        </div>
    )
}
