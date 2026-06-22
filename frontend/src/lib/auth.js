import { api } from './api'

const TOKEN_KEY = 'cc_demo_token'

// Seeded demo user credentials. The backend is expected to have this
// account pre-seeded (see PLAN.md M3 backend ask).
const DEMO_CREDENTIALS = {
    username: 'demo-resident',
    password: 'demo123',
}

/**
 * On app boot: if a token is already stored, reuse it. Otherwise log in as
 * the fixed demo user. Login/Register UI buttons are present elsewhere but
 * intentionally unwired per the locked scope decision in PLAN.md.
 */
export async function ensureDemoSession() {
    const existing = localStorage.getItem(TOKEN_KEY)
    if (existing) {
        return { token: existing, fromCache: true }
    }

    try {
        const res = await api.login(DEMO_CREDENTIALS)
        const token = res?.token || res?.data?.token
        if (token) {
            localStorage.setItem(TOKEN_KEY, token)
            return { token, user: res?.user || res?.data?.user, fromCache: false }
        }
        throw new Error('Login response did not include a token')
    } catch (err) {
        console.warn('Demo auto-login failed; chat will run in offline/mock mode.', err)
        return { token: null, error: err }
    }
}

export function getStoredToken() {
    return localStorage.getItem(TOKEN_KEY)
}

export function clearSession() {
    localStorage.removeItem(TOKEN_KEY)
}