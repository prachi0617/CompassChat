import { create } from 'zustand'
import { ensureDemoSession } from '../lib/auth'
import { connectSocket, disconnectSocket } from '../lib/websocket'

export const useAuthStore = create((set, get) => ({
    token: null,
    user: null,
    status: 'idle', // idle | loading | ready | offline
    socketReady: false,

    boot: async () => {
        if (get().status === 'loading' || get().status === 'ready') return
        set({ status: 'loading' })
        const result = await ensureDemoSession()

        if (result.token) {
            set({ token: result.token, user: result.user || { displayName: 'Demo User' }, status: 'ready' })
            connectSocket({
                onConnect: () => {
                    console.info('[ws] connected')
                    set({ socketReady: true })
                },
                onDisconnect: () => {
                    console.info('[ws] disconnected')
                    set({ socketReady: false })
                },
            })
        } else {
            // Offline/mock mode: chat store falls back to local mock data.
            set({ status: 'offline' })
        }
    },

    teardown: () => {
        disconnectSocket()
        set({ token: null, user: null, status: 'idle', socketReady: false })
    },
}))